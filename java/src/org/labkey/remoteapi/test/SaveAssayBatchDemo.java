package org.labkey.remoteapi.test;

import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.assay.Batch;
import org.labkey.remoteapi.assay.Data;
import org.labkey.remoteapi.assay.Run;
import org.labkey.remoteapi.assay.SaveAssayBatchCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: jeckels
 * Date: Apr 28, 2010
 */
public class SaveAssayBatchDemo
{
    public static void main(String... args) throws Exception
    {
        if (args.length == 0)
        {
            System.err.println("You must include at least one file as a command-line argument");
            System.exit(1);
        }

        // Create a batch
        Batch batch = new Batch();
        batch.setName("Test " + new Date());
        // Optionally set batch properties
//        run.getProperties().put("MyBatchProperty", "BatchPropertyValue");

        // Create a run, add it to the batch
        Run run = new Run();
        run.setName("External usage from " + new Date());
        // Optionally set run properties
//        run.getProperties().put("MyRunProperty", "RunPropertyValue");
        batch.getRuns().add(run);

        // Add each of the files to the run
        System.out.println("Adding a usage of: ");
        List<Data> inputFiles = new ArrayList<Data>();
        for (String arg : args)
        {
            Data data = new Data();
            File file = new File(arg);
            if (!file.exists())
            {
                System.err.println("Could not find file " + arg);
                System.exit(1);
            }
            data.setAbsolutePath(file.getAbsolutePath());
            inputFiles.add(data);
            System.out.println("\t" + arg);
        }
        run.setDataInputs(inputFiles);

        SaveAssayBatchCommand command = new SaveAssayBatchCommand(17486, batch);

        // Execute the command in the /Assay project on the local server
        Connection connection = new Connection("http://localhost/labkey", "assayuser@labkey.com", "testpassword");
        command.execute(connection, "/Assay");
        System.out.println("Success!");
    }

}
