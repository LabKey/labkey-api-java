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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 10:20:49 AM
*/
public class Test
{
    public static void main(String[] args) throws Exception
    {
        Connection cn = new Connection("https://localhost:8443/labkey");

        try
        {
            selectTest(cn);
            //insertTest(cn);
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

        cmd.addSort("LastName", Sort.Direction.DESCENDING);
        cmd.addSort(new Sort("FirstName"));

        //cmd.addFilter(new Filter("LastName", "Stearns"));

        SelectRowsResponse response = cmd.execute(cn, "Api Test");
        System.out.println("Number of rows: " + response.getRowCount());
        List<Map<String,Object>> rows = response.getRows();
        for(Map<String,Object> row : rows)
        {
            System.out.println(row);
        }

        SelectRowsResponse.ColumnDataType type = response.getColumnDataType("Date");
        if(null != type)
            System.out.println("Type of FirstName column is " + type);
        Map<String,Object> row = response.getRows().get(0);
        assert row.get("Date") instanceof Date;

        String idCol = response.getProperty("metaData.id");
        System.out.println("ID column is " + idCol);
    }

    public static void insertTest(Connection cn) throws Exception
    {
        InsertRowsCommand cmd = new InsertRowsCommand("lists", "People");
        Map<String,Object> row = new HashMap<String,Object>();
        row.put("FirstName", "Test");
        row.put("LastName", "Person");
        cmd.addRow(row);
        RowsResponse response = cmd.execute(cn, "Api Test");
        System.out.println(response.getParsedData().toString());
    }
}