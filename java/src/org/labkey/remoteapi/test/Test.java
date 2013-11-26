/*
 * Copyright (c) 2008-2013 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.assay.AssayListCommand;
import org.labkey.remoteapi.assay.AssayListResponse;
import org.labkey.remoteapi.assay.nab.NAbRunsCommand;
import org.labkey.remoteapi.assay.nab.NAbRunsResponse;
import org.labkey.remoteapi.assay.nab.model.NAbNeutralizationResult;
import org.labkey.remoteapi.assay.nab.model.NAbRun;
import org.labkey.remoteapi.assay.nab.model.NAbSample;
import org.labkey.remoteapi.query.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 10:20:49 AM
*/
public class Test
{
    public static void main(String[] args) throws Exception
    {
        Connection cn = new Connection("http://localhost:8080/labkey", args[0], args[1]);

        try
        {
            nabTest(cn);
            selectTest(cn);
            crudTest(cn);
            execSqlTest(cn);
            assayTest(cn);
            schemasTest(cn);
            extendedFormatTest(cn);
            datasetTest(cn);
        }
        catch(CommandException e)
        {
            System.out.println("Command Exception: " + e.getMessage());
        }
    }

    private static void extendedFormatTest(Connection cn) throws Exception
    {
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");
        cmd.setRequiredVersion(9.1);
        SelectRowsResponse resp = cmd.execute(cn, "Api Test/My Subfolder");

        for(Map<String,Object> row : resp.getRows())
        {
            for(Map.Entry<String,Object> entry : row.entrySet())
            {
                Object value = ((JSONObject)entry.getValue()).get("value");
                System.out.println(entry.getKey() + " = " + value + " (type: " + (null == value ? "null" : value.getClass().getName()) + ")");
            }
        }
    }

    private static void schemasTest(Connection cn) throws Exception
    {
        GetSchemasCommand cmd = new GetSchemasCommand();
        GetSchemasResponse resp = cmd.execute(cn, "Api Test");
        for(String name : resp.getSchemaNames())
        {
            System.out.println(name);
        }

        GetQueriesCommand cmdq = new GetQueriesCommand("lists");
        GetQueriesResponse respq = cmdq.execute(cn, "Api Test");
        for(String qname : respq.getQueryNames())
        {
            System.out.println(qname);
            for(String colName : respq.getColumnNames(qname))
            {
                System.out.println("  " + colName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void selectTest(Connection cn) throws Exception
    {
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");

        cmd.addSort(new Sort("LastName"));
        cmd.getColumns().add("FirstName");
        cmd.getColumns().add("LastName");

        SelectRowsResponse response = cmd.execute(cn, "Api Test");
        System.out.println("Number of rows: " + response.getRowCount());

        List<Map<String,Object>> rows = response.getRows();
        for(Map<String,Object> row : rows)
        {
            System.out.println(row);
        }
    }

    public static void nabTest(Connection cn) throws Exception
    {
        double avg = getAverageNeutralization(cn, "/NabVerifyProject", "NAb", 50);
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

    public static void crudTest(Connection cn) throws Exception
    {
        int rowCount = 0;

        //get the current row count
        SelectRowsCommand cmdsel = new SelectRowsCommand("lists", "People");
        SelectRowsResponse srresp = cmdsel.execute(cn, "Api Test");
        rowCount = srresp.getRowCount().intValue();

        //insert a row
        InsertRowsCommand cmdins = new InsertRowsCommand("lists", "People");

        Map<String,Object> row = new HashMap<String,Object>();
        row.put("FirstName", "To Be Inserted");
        row.put("LastName", "Test Inserted Value");

        cmdins.addRow(row);
        SaveRowsResponse resp = cmdins.execute(cn, "Api Test");

        //make sure row count is one greater
        srresp = cmdsel.execute(cn, "Api Test");
        assert srresp.getRowCount().intValue() == rowCount + 1;
        assert srresp.getRows().get(srresp.getRows().size() - 1).get("FirstName").equals("To Be Inserted");

        //update the newly-added row
        UpdateRowsCommand cmdupd = new UpdateRowsCommand("lists", "People");
        row = resp.getRows().get(resp.getRows().size() - 1);
        row.put("LastName", "Test UPDATED");
        cmdupd.addRow(row);
        resp = cmdupd.execute(cn, "Api Test");
        assert resp.getRowsAffected().intValue() == 1;
        assert resp.getRows().get(resp.getRows().size() - 1).get("LastName").equals("Test UPDATED");

        //delete the newly added row
        DeleteRowsCommand cmddel = new DeleteRowsCommand("lists", "People");
        cmddel.addRow(row);
        resp = cmddel.execute(cn, "Api Test");
        assert resp.getRowsAffected().intValue() == 1;
        
        //assert that the row count is back to what it was
        srresp = cmdsel.execute(cn, "Api Test");
        assert srresp.getRowCount().intValue() == rowCount;
    }

    public static void assayTest(Connection cn) throws Exception
    {
        AssayListCommand cmd = new AssayListCommand();
        AssayListResponse resp = cmd.execute(cn, "Study Test");
        System.out.println(resp.getDefinitions());
    }

    public static void execSqlTest(Connection cn) throws Exception
    {
        ExecuteSqlCommand cmd = new ExecuteSqlCommand("lists");
        cmd.setSql("select People.FirstName, count(People.FirstName) as Num from People group by People.FirstName");
        SelectRowsResponse resp = cmd.execute(cn, "Api Test");
        System.out.println(resp.getRows());
    }

    // Uses the StudyTest artifacts with security changed to editable datasets
    public static void datasetTest(Connection cn) throws Exception
    {
        SelectRowsCommand select = new SelectRowsCommand("study", "DataSets");
        SelectRowsResponse response = select.execute(cn, "StudyVerifyProject/My Study");

        List<Map<String, Object>> rows = response.getRows();

        for (Map<String, Object> row : rows)
            System.out.println(row);

        for (String queryName : new String[]{"DEM-1", "DEM-1: Demographics"})
        {
            SelectRowsCommand datasetRowSelect = new SelectRowsCommand("study", queryName);
            datasetRowSelect.addFilter(new Filter("MouseId", "999320565"));
            SelectRowsResponse datasetRow = datasetRowSelect.execute(cn, "StudyVerifyProject/My Study");
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
}