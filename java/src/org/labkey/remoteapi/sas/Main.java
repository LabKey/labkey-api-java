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

        command.addFilter("Age", "GREATER_THAN_OR_EQUAL_TO", "11");
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
        System.out.println("Insert new rows");
        System.out.println();
        SASInsertRowsCommand insert = new SASInsertRowsCommand("lists", "People");
        SASRow row = new SASRow();
        row.clear();
        row.add("First", "Pebbles");
        row.add("Last", "Flintstone");

        insert.addRow(row);

        SASSaveRowsResponse resp = new SASSaveRowsResponse(cn, insert, "home");

        System.out.println("Inserted " + resp.getRowsAffected() + " rows.");

/*
        System.out.println();
        System.out.println("NAB data from Atlas");
        System.out.println();
        cn = new SASConnection("https://atlas.scharp.org/cpas");
        command = new SASSelectRowsCommand("study", "Monogram NAb");
        command.setColumns("ConcentrationValue, PercentInhibition");
        response = new SASSelectRowsResponse(cn, command, "/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project");
        logResponse(response);
*/
    }

    private static void logResponse(SASSelectRowsResponse response)
    {
        int columnCount = response.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            if (!response.isHidden(i))
            {
                String column = response.getColumnName(i);
                System.out.println(column + ": " + response.getType(column));
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
                        else
                        {
                            line.append(dataResponse.getNumeric(column));
                        }
                    }
                }

                line.append(" ");
            }

            System.out.println(line);
        }
    }
}

