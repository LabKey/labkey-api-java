/*
 * Copyright (c) 2009-2016 LabKey Corporation
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

package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.CommandException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * User: adam
 * Date: Dec 20, 2008
 * Time: 1:06:10 PM
 */
public class Main
{
    public static void main(String[] args)
    {
        try
        {
            test();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
        Tests the SAS wrapper api by simulating in Java the calls made by the SAS macros. Requires the following:

        - Local, running LabKey Server with .netrc/_netrc configured with localhost credentials that grant editor role in /home
        - A list called "People" defined in /home with a custom grid view "males" that filters to Gender = 1
     */
    private static void test() throws CommandException, IOException, URISyntaxException
    {
        String folder = "home";
        SASConnection cn = new SASConnection("http://localhost:8080/labkey");

        System.out.println();
        System.out.println("All People");
        System.out.println();
        SASSelectRowsCommand command = new SASSelectRowsCommand("lists", "People");
        command.setMaxRows(0);
        SASSelectRowsResponse response = new SASSelectRowsResponse(cn, command, folder);
        logResponse(response);

        command.addFilter("Age", "GREATER_THAN_OR_EQUAL", "12");
        response = new SASSelectRowsResponse(cn, command, folder);
        System.out.println();
        System.out.println("Old people");
        System.out.println();
        logResponse(response);

        System.out.println();
        System.out.println("Specify view, columns, sort, maxRows, and offset");
        System.out.println();
        command = new SASSelectRowsCommand("lists", "People");
        command.setViewName("males");
        command.setColumns("First, Last");
        command.setSorts("Last, -First");
        command.setMaxRows(3.0);
        command.setOffset(1.0);
        response = new SASSelectRowsResponse(cn, command, folder);
        logResponse(response);

        System.out.println();
        System.out.println("Test executeSql");
        System.out.println();
        SASExecuteSqlCommand execSql = new SASExecuteSqlCommand("lists", "SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight, AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last");
        response = new SASSelectRowsResponse(cn, execSql, folder);
        logResponse(response);

        System.out.println();
        System.out.println("Insert new rows");
        System.out.println();
        SASInsertRowsCommand insert = new SASInsertRowsCommand("lists", "People");

        SASRow row = new SASRow();
        row.put("First", "Pebbles");
        row.put("Last", "Flintstone");
        row.put("Age", 1);
        row.put("Appearance", "1963-02-22");
        insert.addRow(row);

        row = new SASRow();
        row.put("First", "Bamm-Bamm");
        row.put("Last", "Rubble");
        row.put("Age", 1);
        row.put("Appearance", "1963-10-01");
        insert.addRow(row);

        SASSaveRowsResponse resp = new SASSaveRowsResponse(cn, insert, folder);

        System.out.println("Inserted " + resp.getRowsAffected() + " rows.");

        System.out.println();
        System.out.println("Display new rows");
        System.out.println();
        command = new SASSelectRowsCommand("lists", "People");
        response = new SASSelectRowsResponse(cn, command, folder);
        logResponse(response);

        System.out.println();
        System.out.println("Delete new rows");
        System.out.println();
        command.setColumns("Key");
        command.addFilter("Age", "LESS_THAN_OR_EQUAL", "1");
        response = new SASSelectRowsResponse(cn, command, folder);
        SASDeleteRowsCommand delete = new SASDeleteRowsCommand("lists", "People");

        while (response.getRow())
        {
            row = new SASRow();
            row.put("Key", response.getNumeric("Key"));
            delete.addRow(row);
        }

        resp = new SASSaveRowsResponse(cn, delete, folder);
        System.out.println("Deleted " + resp.getRowsAffected() + " rows.");
        System.out.println("*** All test passed without error ***");
    }

    private static void logResponse(SASSelectRowsResponse response)
    {
        String missingCode = response.getMissingValuesCode();
        System.out.println(missingCode);

        int columnCount = response.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            if (!response.isHidden(i))
            {
                String column = response.getColumnName(i);
                String type = response.getType(column);
                System.out.println(column + " (" + response.getLabel(i) + ")" + ": " + type + ("STRING".equals(type) ? " " + response.getScale(i) : ""));
            }
        }

        while (response.getRow())
        {
            StringBuilder line = new StringBuilder();

            for (int i = 0; i < columnCount; i++)
            {
                if (response.isHidden(i))
                    continue;

                String column = response.getColumnName(i);
                String type = response.getType(column);

                if ("STRING".equals(type))
                {
                    line.append(response.getCharacter(column));
                }
                else
                {
                    if (response.isNull(column))
                    {
                        line.append("null");
                    }
                    else
                    {
                        if ("DATE".equals(type))
                        {
                            line.append(response.getDate(column));
                        }
                        else if ("BOOLEAN".equals(type))
                        {
                            line.append(response.getBoolean(column));
                        }
                        else
                        {
                            line.append(response.getNumeric(column));
                        }
                    }
                }

                if (response.allowsMissingValues(column))
                {
                    String qc = response.getMissingValue(column);

                    if (null != qc)
                        line.append(" (").append(qc).append(")");
                }

                line.append(" ");
            }

            System.out.println(line);
        }
    }
}

