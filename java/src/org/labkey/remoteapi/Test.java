/*
 * Copyright (c) 2008 LabKey Corporation
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
package org.labkey.remoteapi;

import org.labkey.remoteapi.query.*;
import org.labkey.remoteapi.assay.AssayListCommand;
import org.labkey.remoteapi.assay.AssayListResponse;

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
        Connection cn = new Connection("http://localhost:8080/labkey");

        try
        {
            selectTest(cn);
            crudTest(cn);
            //assayTest(cn);
        }
        catch(CommandException e)
        {
            System.out.println("Command Exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void selectTest(Connection cn) throws Exception
    {
        SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");

        cmd.addSort(new Sort("LastName"));

        SelectRowsResponse response = cmd.execute(cn, "Api Test");
        System.out.println("Number of rows: " + response.getRowCount());

        List<Map<String,Object>> rows = response.getRows();
        for(Map<String,Object> row : rows)
        {
            System.out.println(row);
        }
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
        row.put("FirstName", "Insert");
        row.put("LastName", "Test");

        cmdins.addRow(row);
        SaveRowsResponse resp = cmdins.execute(cn, "Api Test");

        //make sure row count is one greater
        srresp = cmdsel.execute(cn, "Api Test");
        assert srresp.getRowCount().intValue() == rowCount + 1;

        //update the newly-added row
        UpdateRowsCommand cmdupd = new UpdateRowsCommand("lists", "People");
        row = resp.getRows().get(0);
        row.put("LastName", "Test UPDATED");
        cmdupd.addRow(row);
        resp = cmdupd.execute(cn, "Api Test");
        assert resp.getRowsAffected().intValue() == 1;
        assert resp.getRows().get(0).get("LastName").equals("Test UPDATED");

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

}