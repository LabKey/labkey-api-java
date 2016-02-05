/*
 * Copyright (c) 2010-2016 LabKey Corporation
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
import org.labkey.remoteapi.assay.Batch;
import org.labkey.remoteapi.assay.Data;
import org.labkey.remoteapi.assay.Run;
import org.labkey.remoteapi.assay.SaveAssayBatchCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Five command line args:
 * 1. File name
 * 2. Description string
 * 3. GPAT protocol's assayid/rowid
 * 4. Server base URL (http://www.myserver.com/labkey)
 * 5. Path to target folder on server
 * User: jeckels
 * Date: Apr 28, 2010
 */
public class SaveAssayBatchDemo
{
    public static void main(String... args) throws Exception
    {
//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
        
        if (args.length != 5 && args.length != 7)
        {
            System.err.println("Invalid arguments");
            if (args.length == 6)
            {
                System.err.println("If you specify a username, you must also specify a password");
            }
            
            printUsage();
            System.exit(1);
        }

        String fileName = args[0];
        
        String description = args[1].trim();
        if (description.length() == 0)
        {
            System.err.println("Invalid usage description");
            printUsage();
            System.exit(1);
        }
        
        int assayId;
        try
        {
            assayId = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            System.err.println("Could not parse assayId " + args[2]);
            printUsage();
            System.exit(1);
            return;
        }
        
        String baseServerURL = args[3];
        if (!baseServerURL.toLowerCase().startsWith("http"))
        {
            System.err.println("Invalid base server URL, expected it to start with http or https " + args[3]);
            printUsage();
            System.exit(1);
        }
        
        String folderPath = args[4];
        if (!folderPath.startsWith("/"))
        {
            folderPath = "/" + folderPath;
        }

        // Create a batch
        Batch batch = new Batch();
        batch.setName("Batch " + description);
        // Optionally set batch properties
//        run.getProperties().put("MyBatchProperty", "BatchPropertyValue");

        // Create a run, add it to the batch
        Run run = new Run();
        run.setName(description);
        // Optionally set run properties
//        run.getProperties().put("MyRunProperty", "RunPropertyValue");
        run.getProperties().put("MyDate", new java.util.Date());  //Issue: 23708.
        batch.getRuns().add(run);

        // Add each of the files to the run
        List<Data> inputFiles = new ArrayList<Data>();
        Data data = new Data();
        data.setAbsolutePath(fileName);
        System.out.println("Adding usage \"" + description + " \" for file \"" + fileName + "\"");
        inputFiles.add(data);
        run.setDataInputs(inputFiles);

        SaveAssayBatchCommand command = new SaveAssayBatchCommand(assayId, batch);
        
        // Execute the command
        Connection connection; 
        if (args.length == 7)
        {
            connection = new Connection(baseServerURL, args[5], args[6]);
        }
        else
        {
            connection = new Connection(baseServerURL);
        }
        try
        {
            command.execute(connection, folderPath);
            System.out.println("Success!");
        }
        catch (CommandException e)
        {
            System.err.println("Failure! Response code: " + e.getStatusCode());
            e.printStackTrace();
            System.err.println();
            if (e.getResponseText() != null)
            {
                System.err.println("Response text: ");
                System.err.println(e.getResponseText());
            }
        }
    }

    private static void printUsage()
    {
        System.err.println();
        System.err.println("Expected usage: java " + SaveAssayBatchDemo.class.getName() + " [FILE_NAME] [DESCRIPTION] [ASSAY_ID] [BASE_SERVER_URL] [TARGET_FOLDER] <USERNAME> <PASSWORD>");
        System.err.println("\t[FILE_NAME]:       relative path to the file to be marked by this usage; the web server must see the file at the same path");
        System.err.println("\t[DESCRIPTION]:     string to be used as the name of this usage");
        System.err.println("\t[ASSAY_ID]:        the assay ID (RowID) of the assay definition to be used for this usage");
        System.err.println("\t[BASE_SERVER_URL]: URL of the LabKey Server instance in which to store the usage, for example: https://www.myserver.com/labkey");
        System.err.println("\t[TARGET_FOLDER]:   target project and folder where the usage should be stored, for example: /MyProject/MyFolder");
        System.err.println("\t<USERNAME>:        (optional) username with which to authenticate");
        System.err.println("\t<PASSWORD>:        (optional) password with which to authenticate");
    }

}
