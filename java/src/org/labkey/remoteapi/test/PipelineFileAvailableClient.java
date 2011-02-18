package org.labkey.remoteapi.test;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.pipeline.FileNotificationCommand;

/**
 * Demo code that notifies server that files may become available under a pipeline root, which releases
 * queued pipeline jobs that are blocked awaiting those files.
 *
 * By default looks for a config.properties in the working directory, but the location can be specified with a
 * -config=<PATH TO CONFIG FILE> argument.
 *
 * Expects the following config properties:
 * baseServerURL: base URL of the LabKey Server, such as "http://www.labkey.org"
 * username: credentials for logging in
 * password: credentials for logging in
 * folder: Path of the LabKey Server container to which the notification should be directed
 * debug: (Optional) If true, print verbose HTTP connection debugging information
 *
 */
public class PipelineFileAvailableClient
{

    public static void main(String... args) throws Exception
    {
        ClientConfig config = new ClientConfig(args);
        Connection connection = config.createConnection();

        try
        {
            // Create the command an execute it against our target folder
            FileNotificationCommand notificationCommand = new FileNotificationCommand();
            notificationCommand.execute(connection, config.getProperty("folder"));
            System.out.println("Success!");
        }
        catch(CommandException e)
        {
            System.err.println((new StringBuilder()).append("Failure! Response code: ").append(e.getStatusCode()).toString());
            e.printStackTrace();
            System.err.println();
            if(e.getResponseText() != null)
            {
                System.err.println("Response text: ");
                System.err.println(e.getResponseText());
            }
        }
    }
}
