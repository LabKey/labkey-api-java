package org.labkey.remoteapi.test;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.assay.ImportRunCommand;
import org.labkey.remoteapi.assay.ImportRunResponse;

import java.io.File;
import java.util.Collections;

/**
 * User: kevink
 * Date: 9/12/12
 */
public class ImportRunDemo
{
    public static void main(String... args) throws Exception
    {
        String baseServerURL = "http://localhost:8080/labkey/";
        String folderPath = "/Flow/FCSExpress";
        String username = "kevink@labkey.com";
        String password = "xxxxxx";

        boolean useJson = false;
        int assayId = 3305;
        int batchId = 0;
        String name = "java api run";
        String comment = "woohoo!";
        File file = new File("/Users/kevink/data/DeNovo/BatchExport.xml");

        ImportRunCommand command = new ImportRunCommand(assayId, file);
        command.setBatchId(batchId);
        command.setName(name);
        command.setComment(comment);
        command.setProperties(Collections.<String, Object>singletonMap("RunPropOne", "Hello World"));
        command.setBatchProperties(Collections.<String, Object>singletonMap("BatchPropOne", "Willy Wonka"));
        command.setUseJson(useJson);

        // Execute the command
        Connection connection;
        if (username != null && password != null)
        {
            connection = new Connection(baseServerURL, username, password);
        }
        else
        {
            connection = new Connection(baseServerURL);
        }

        try
        {
            ImportRunResponse resp = command.execute(connection, folderPath);
            System.out.println("Success!");
            System.out.println("  url:     " + resp.getSuccessURL());
            System.out.println("  assayId: " + resp.getAssayId());
            System.out.println("  batchId: " + resp.getBatchId());
            System.out.println("  runId:   " + resp.getRunId());
        }
        catch (CommandException e)
        {
            System.out.println("Error! Response code: " + e.getStatusCode());
            e.printStackTrace();
            System.out.println();
            if (e.getResponseText() != null)
            {
                System.out.println("Response text: ");
                System.out.println(e.getResponseText());
            }
        }
    }
}
