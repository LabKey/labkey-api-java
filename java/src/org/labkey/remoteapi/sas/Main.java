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
        Tests the SAS wrapper api by simulating in Java the calls made by the SAS macros.  Requires the following:

        - Local, running LabKey Server with a list called People defined in /home and either guest read
          permissions to /home or .netrc/_netrc configured with localhost credentials.
        - Access to https://atlas.scharp.org and .netrc/_netrc configured with Atlas credentials that have
          read permissions to the specified folder.
     */
    private static void test() throws CommandException, IOException, URISyntaxException
    {
        SASConnection cn = new SASConnection("http://localhost:8080/labkey");

        System.out.println();
        System.out.println("All People");
        System.out.println();
        SASSelectRowsCommand command = new SASSelectRowsCommand("lists", "People");
        SASSelectRowsResponse response = new SASSelectRowsResponse(cn, command, "home");
        logResponse(response);

        command.addFilter("Age", "GREATER_THAN_OR_EQUAL_TO", "12");
        response = new SASSelectRowsResponse(cn, command, "home");
        System.out.println();
        System.out.println("Old people");
        System.out.println();
        logResponse(response);

        System.out.println();
        System.out.println("Specify view, columns, sort, maxRows, and offset");
        System.out.println();
        command = new SASSelectRowsCommand("lists", "People");
        command.setViewName("namesByAge");
        command.setColumns("First, Last");
        command.setSorts("Last, -First");
        command.setMaxRows(3.0);
        command.setOffset(1.0);
        response = new SASSelectRowsResponse(cn, command, "home");
        logResponse(response);

        System.out.println();
        System.out.println("Test executeSql");
        System.out.println();
        SASExecuteSqlCommand execSql = new SASExecuteSqlCommand("lists", "SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight, AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last");
        response = new SASSelectRowsResponse(cn, execSql, "home");
        logResponse(response);

        System.out.println();
        System.out.println("Insert new rows");
        System.out.println();
        SASInsertRowsCommand insert = new SASInsertRowsCommand("lists", "People");

        SASRow row = new SASRow();
        row.put("First", "Pebbles");
        row.put("Last", "Flintstone");
        row.put("Age", 1);
        row.putDate("Appearance", 1148);
        insert.addRow(row);

        row = new SASRow();
        row.put("First", "Bamm-Bamm");
        row.put("Last", "Rubble");
        row.put("Age", 1);
        row.putDate("Appearance", 1369);
        insert.addRow(row);

        SASSaveRowsResponse resp = new SASSaveRowsResponse(cn, insert, "home");

        System.out.println("Inserted " + resp.getRowsAffected() + " rows.");

        System.out.println();
        System.out.println("Display new rows");
        System.out.println();
        command = new SASSelectRowsCommand("lists", "People");
        response = new SASSelectRowsResponse(cn, command, "home");
        logResponse(response);

        System.out.println();
        System.out.println("Delete new rows");
        System.out.println();
        command.setColumns("Key");
        command.addFilter("Age", "LESS_THAN_OR_EQUAL_TO", "1");
        response = new SASSelectRowsResponse(cn, command, "home");
        SASDeleteRowsCommand delete = new SASDeleteRowsCommand("lists", "People");

        String key = response.stash();
        response = new SASSelectRowsResponse(key);

        while (response.getRow())
        {
            row = new SASRow();
            row.put("Key", response.getNumeric("Key"));
            delete.addRow(row);
        }

        resp = new SASSaveRowsResponse(cn, delete, "home");
        System.out.println("Deleted " + resp.getRowsAffected() + " rows.");
    }

    private static void logResponse(SASSelectRowsResponse response)
    {
        int columnCount = response.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            if (!response.isHidden(i))
            {
                String column = response.getColumnName(i);
                String type = response.getType(column);
                System.out.println(column + ": " + type + ("STRING".equals(type) ? " " + response.getScale(i) : ""));
            }
        }

        String key = response.stash();

        SASSelectRowsResponse dataResponse = new SASSelectRowsResponse(key);

        while (dataResponse.getRow())
        {
            StringBuilder line = new StringBuilder();

            for (int i = 0; i < columnCount; i++)
            {
                if (response.isHidden(i))
                    continue;

                String column = dataResponse.getColumnName(i);
                String type = dataResponse.getType(column);

                if ("STRING".equals(type))
                {
                    line.append(dataResponse.getCharacter(column));
                }
                else
                {
                    if (dataResponse.isNull(column))
                    {
                        line.append("null");
                    }
                    else
                    {
                        if ("DATE".equals(type))
                        {
                            line.append(dataResponse.getDate(column));
                        }
                        else if ("BOOLEAN".equals(type))
                        {
                            line.append(dataResponse.getBoolean(column));
                        }
                        else
                        {
                            line.append(dataResponse.getNumeric(column));
                        }
                    }
                }

                if (dataResponse.allowsQC(column))
                {
                    String qc = dataResponse.getQCIndicator(column);

                    if (null != qc)
                        line.append(" (").append(qc).append(")");
                }

                line.append(" ");
            }

            System.out.println(line);
        }
    }
}
