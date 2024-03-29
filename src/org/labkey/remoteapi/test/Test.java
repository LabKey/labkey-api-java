/*
 * Copyright (c) 2008-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.test;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.assay.AssayListCommand;
import org.labkey.remoteapi.assay.AssayListResponse;
import org.labkey.remoteapi.assay.nab.NAbRunsCommand;
import org.labkey.remoteapi.assay.nab.NAbRunsResponse;
import org.labkey.remoteapi.assay.nab.model.NAbNeutralizationResult;
import org.labkey.remoteapi.assay.nab.model.NAbRun;
import org.labkey.remoteapi.assay.nab.model.NAbSample;
import org.labkey.remoteapi.query.ContainerFilter;
import org.labkey.remoteapi.query.DeleteRowsCommand;
import org.labkey.remoteapi.query.ExecuteSqlCommand;
import org.labkey.remoteapi.query.Filter;
import org.labkey.remoteapi.query.GetQueriesCommand;
import org.labkey.remoteapi.query.GetQueriesResponse;
import org.labkey.remoteapi.query.GetSchemasCommand;
import org.labkey.remoteapi.query.GetSchemasResponse;
import org.labkey.remoteapi.query.InsertRowsCommand;
import org.labkey.remoteapi.query.SaveRowsResponse;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.remoteapi.query.Sort;
import org.labkey.remoteapi.query.TruncateTableCommand;
import org.labkey.remoteapi.query.TruncateTableResponse;
import org.labkey.remoteapi.query.UpdateRowsCommand;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test
{
    private static final String LIST_FOLDER = "Api Test";
    private static final String STUDY_FOLDER = "StudyVerifyProject/My Study";
    private static final String NAB_FOLDER = "/Nab Test Verify Project/nabassay";

    public static void main(String[] args) throws Exception
    {
        String baseUrl = "http://localhost:8080/labkey";
        Connection cn = args.length < 2 ? new Connection(baseUrl) : new Connection(baseUrl, args[0], args[1]);
        //Connection cn = new Connection(baseUrl, new ApiKeyCredentialsProvider("session:d7c3a4aeb283e3e54c4126a707908420"));
        //Connection cn = new Connection(baseUrl, new NetRcCredentialsProvider(baseUrl));

        try
        {
            // Import /remoteapi/labkey-api-java/People.xls as list "People" into project "Api Test"
            selectTest(cn, LIST_FOLDER);
            crudTest(cn, LIST_FOLDER);
            execSqlTest(cn, LIST_FOLDER);
            schemasTest(cn, LIST_FOLDER);
            extendedFormatTest(cn, LIST_FOLDER);

            // Run ShortStudyTest with -Dclean=false
            datasetTest(cn, STUDY_FOLDER);

            // Run NabAssayTest with -Dclean=false and rename subfolder "Rename############" to "nabassay"
            nabTest(cn, NAB_FOLDER);
            assayTest(cn, NAB_FOLDER);
            truncateAssayFailsTest(cn, NAB_FOLDER);

            truncateTableSuccessTest(cn, LIST_FOLDER);
            System.out.println("*** All tests completed successfully ***");
        }
        catch(CommandException e)
        {
            e.printStackTrace();
        }
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    public static void selectTest(Connection cn, String folder) throws Exception
    {
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");

        cmd.addSort(new Sort("Last"));
        cmd.getColumns().add("First");
        cmd.getColumns().add("Last");

        SelectRowsResponse response = cmd.execute(cn, folder);
        System.out.println("Number of rows: " + response.getRowCount());

        List<Map<String, Object>> rows = response.getRows();

        for (Map<String, Object> row : rows)
        {
            System.out.println(row);
        }
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    public static void crudTest(Connection cn, String folder) throws Exception
    {
        int rowCount = 0;

        //get the current row count
        SelectRowsCommand cmdsel = new SelectRowsCommand("lists", "People");
        SelectRowsResponse srresp = cmdsel.execute(cn, folder);
        rowCount = srresp.getRowCount().intValue();

        //insert a row
        InsertRowsCommand cmdins = new InsertRowsCommand("lists", "People");

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("First", "To Be Inserted");
        row.put("Last", "Test Inserted Value");

        cmdins.addRow(row);
        SaveRowsResponse resp = cmdins.execute(cn, folder);

        //make sure row count is one greater
        srresp = cmdsel.execute(cn, folder);
        assert srresp.getRowCount().intValue() == rowCount + 1;
        assert srresp.getRows().get(srresp.getRows().size() - 1).get("First").equals("To Be Inserted");

        //update the newly-added row
        UpdateRowsCommand cmdupd = new UpdateRowsCommand("lists", "People");
        row = resp.getRows().get(resp.getRows().size() - 1);
        row.put("LastName", "Test UPDATED");
        cmdupd.addRow(row);
        resp = cmdupd.execute(cn, folder);
        assert resp.getRowsAffected().intValue() == 1;
        assert resp.getRows().get(resp.getRows().size() - 1).get("Last").equals("Test UPDATED");

        //delete the newly added row
        DeleteRowsCommand cmddel = new DeleteRowsCommand("lists", "People");
        cmddel.addRow(row);
        resp = cmddel.execute(cn, folder);
        assert resp.getRowsAffected().intValue() == 1;

        //assert that the row count is back to what it was
        srresp = cmdsel.execute(cn, folder);
        assert srresp.getRowCount().intValue() == rowCount;
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    public static void truncateTableSuccessTest(Connection cn, String folder) throws Exception
    {
        TruncateTableCommand trunc = new TruncateTableCommand("lists", "People");
        TruncateTableResponse resp = trunc.execute(cn, folder);

        assert resp.getDeletedRowCount() == 9;
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    public static void execSqlTest(Connection cn, String folder) throws Exception
    {
        ExecuteSqlCommand cmd = new ExecuteSqlCommand("lists");
        cmd.setSql("SELECT Last, COUNT(*) AS Num FROM People GROUP BY Last");
        SelectRowsResponse resp = cmd.execute(cn, folder);
        System.out.println(resp.getRows());
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    private static void schemasTest(Connection cn, String folder) throws Exception
    {
        GetSchemasCommand cmd = new GetSchemasCommand();
        GetSchemasResponse resp = cmd.execute(cn, folder);
        for (String name : resp.getSchemaNames())
        {
            System.out.println(name);
        }

        GetQueriesCommand cmdq = new GetQueriesCommand("lists");
        GetQueriesResponse respq = cmdq.execute(cn, folder);
        for (String qname : respq.getQueryNames())
        {
            System.out.println(qname);
            for (String colName : respq.getColumnNames(qname))
            {
                System.out.println("  " + colName);
            }
        }
    }

    // Assumes that /remoteapi/labkey-api-java/People.xls has been imported as a list into folder
    private static void extendedFormatTest(Connection cn, String folder) throws Exception
    {
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");
        cmd.setRequiredVersion(9.1);
        SelectRowsResponse resp = cmd.execute(cn, folder);

        for (Map<String, Object> row : resp.getRows())
        {
            for (Map.Entry<String, Object> entry : row.entrySet())
            {
                Object value = ((Map<String, Object>)entry.getValue()).get("value");
                System.out.println(entry.getKey() + " = " + value + " (type: " + (null == value ? "null" : value.getClass().getName()) + ")");
            }
        }
    }

    // Assumes StudyShortTest has been run with -Dclean=false
    public static void datasetTest(Connection cn, String folder) throws Exception
    {
        SelectRowsCommand select = new SelectRowsCommand("study", "DataSets");
        SelectRowsResponse response = select.execute(cn, folder);

        List<Map<String, Object>> rows = response.getRows();

        for (Map<String, Object> row : rows)
            System.out.println(row);

        for (String queryName : new String[]{"DEM-1", "DEM-1: Demographics"})
        {
            SelectRowsCommand datasetRowSelect = new SelectRowsCommand("study", queryName);
            datasetRowSelect.addFilter(new Filter("MouseId", "999320565"));
            SelectRowsResponse datasetRow = datasetRowSelect.execute(cn, folder);
            Map<String, Object> row = datasetRow.getRows().get(0);
            System.out.println(row);
            if (!row.get("DEMracox").equals("Brazilian"))
                throw new RuntimeException("Race is not 'Brazilian' before update");

            UpdateRowsCommand update = new UpdateRowsCommand("study", queryName);
            row.put("DEMracox", "Martian");
            update.setRows(Collections.singletonList(row));
            update.execute(cn, "StudyVerifyProject/My Study");
            datasetRowSelect = new SelectRowsCommand("study", queryName);
            datasetRowSelect.addFilter(new Filter("MouseId", "999320565"));
            datasetRow = datasetRowSelect.execute(cn, "StudyVerifyProject/My Study");
            row = datasetRow.getRows().get(0);
            System.out.println(row);
            if (!row.get("DEMracox").equals("Martian"))
                throw new RuntimeException("Race is not 'Martian' after first update");

            update = new UpdateRowsCommand("study", queryName);
            row.put("DEMracox", "Brazilian");
            update.setRows(Collections.singletonList(row));
            update.execute(cn, "StudyVerifyProject/My Study");
            datasetRowSelect = new SelectRowsCommand("study", queryName);
            datasetRowSelect.addFilter(new Filter("MouseId", "999320565"));
            datasetRow = datasetRowSelect.execute(cn, "StudyVerifyProject/My Study");
            row = datasetRow.getRows().get(0);
            System.out.println(row);
            if (!row.get("DEMracox").equals("Brazilian"))
                throw new RuntimeException("Race is not 'Brazilian' after second update");
        }
    }

    // Assumes that NabAssayTest has been run with -Dclean=false
    public static void nabTest(Connection cn, String folder) throws Exception
    {
        double avg = getAverageNeutralization(cn, folder, "TestAssayNab", 50);
    }

    public static double getAverageNeutralization(Connection cn, String folderPath, String assayName, int cutoff) throws CommandException, IOException
    {
        NAbRunsCommand nabCommand = new NAbRunsCommand();
        nabCommand.setContainerFilter(ContainerFilter.CurrentAndSubfolders);
        nabCommand.setAssayName(assayName);
        nabCommand.setCalculateNeut(true);
        nabCommand.setIncludeFitParameters(false);
        nabCommand.setIncludeStats(false);
        nabCommand.setIncludeWells(false);
       // nabCommand.addFilter(new Filter("Name", "m0902051", Filter.Operator.STARTS_WITH));
        nabCommand.addSort(new Sort("VirusName", Sort.Direction.ASCENDING));
        NAbRunsResponse runResponse = nabCommand.execute(cn, folderPath);
        NAbRun[] runs = runResponse.getRuns();
        int totalSamples = 0;
        double totalIC50CurveNeutralization = 0;

        for (NAbRun run : runs)
        {
            for (NAbSample sample : run.getSamples())
            {
                long objectId = sample.getObjectId();
                for (NAbNeutralizationResult neutResult : sample.getNeutralizationResults())
                {
                    // only total non-infinite values (that is, results where we found a neutralizing dilution:
                    if (neutResult.getCutoff() == cutoff && !Double.isInfinite(neutResult.getCurveBasedDilution()))
                    {
                        totalSamples++;
                        totalIC50CurveNeutralization += neutResult.getCurveBasedDilution();
                    }
                }
            }
        }

        return totalIC50CurveNeutralization/totalSamples;
    }

    // Assumes that folder has been populated with assays
    public static void assayTest(Connection cn, String folder) throws Exception
    {
        AssayListCommand cmd = new AssayListCommand();
        AssayListResponse resp = cmd.execute(cn, folder);
        System.out.println(resp.getDefinitions());
    }

    // Assumes that folder has been populated with assays
    public static void truncateAssayFailsTest(Connection cn, String folder) throws Exception
    {
        TruncateTableCommand trunc = new TruncateTableCommand("assay.NAb.TestAssayNab", "WellData");
        try
        {
            TruncateTableResponse resp = trunc.execute(cn, folder);
            throw new RuntimeException("Truncate table command should not succeed for tables other than lists," +
                    "datasets, sample sets, data classes, and issues");
        }
        catch (CommandException e)
        {
            assert e.getStatusCode() == 500;
            assert e.getLocalizedMessage().equals("The query 'WellData' in the schema 'assay.NAb.TestAssayNab' is not truncatable.");
        }
    }
}
