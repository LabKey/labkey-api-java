package org.labkey.remoteapi.test;

import org.labkey.remoteapi.ApiKeyCredentialsProvider;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.domain.CreateDomainCommand;
import org.labkey.remoteapi.domain.Domain;
import org.labkey.remoteapi.domain.PropertyDescriptor;
import org.labkey.remoteapi.query.InsertRowsCommand;
import org.labkey.remoteapi.query.QuerySaveRowsCommand;
import org.labkey.remoteapi.query.QuerySaveRowsCommand.Command;
import org.labkey.remoteapi.query.QuerySaveRowsCommand.CommandType;
import org.labkey.remoteapi.query.QuerySaveRowsResponse;
import org.labkey.remoteapi.query.SaveRowsCommand;
import org.labkey.remoteapi.query.SaveRowsResponse;
import org.labkey.remoteapi.security.CreateContainerCommand;
import org.labkey.remoteapi.security.DeleteContainerCommand;

import java.util.List;
import java.util.Map;

public class QuerySaveRowsCommandDemo
{
    public static void main(String[] args) throws Exception
    {
        String folderPath = "SaveRowsCommandDemo";
        String schemaName = "lists";
        String queryName = "Players";

        ApiKeyCredentialsProvider credentials = new ApiKeyCredentialsProvider("xxx");
        Connection conn = new Connection("http://localhost:8080", credentials);

        // Create the project
        new CreateContainerCommand(folderPath).execute(conn, "/");

        try
        {
            // Create a list
            {
                CreateDomainCommand createdListCmd = new CreateDomainCommand("IntList", queryName);
                createdListCmd.setOptions(Map.of("keyName", "JerseyNumber", "keyType", "Integer"));

                Domain domain = createdListCmd.getDomainDesign();
                domain.setFields(List.of(
                    new PropertyDescriptor("FirstName", "First Name", "string"),
                    new PropertyDescriptor("LastName", "Last Name", "string"),
                    new PropertyDescriptor("Team", null, "string")
                ));

                createdListCmd.execute(conn, folderPath);
            }

            // Add some initial data
            {
                InsertRowsCommand insertCmd = new InsertRowsCommand(schemaName, queryName);
                insertCmd.addRow(Map.of("FirstName", "Alvin", "LastName", "David", "JerseyNumber", 21, "Team", "Seattle Mariners"));
                insertCmd.addRow(Map.of("FirstName", "Jay", "LastName", "Buhner", "JerseyNumber", 19, "Team", "New York Yankees"));
                insertCmd.addRow(Map.of("FirstName", "Ken", "LastName", "Phelps", "JerseyNumber", 44, "Team", "Seattle Mariners"));

                SaveRowsResponse response = insertCmd.execute(conn, folderPath);
                System.out.printf(String.format("Inserted %d players%n", response.getRowsAffected().intValue()));
            }

            // Execute multiple query operations using a saveRows command
            {
                QuerySaveRowsCommand saveCmd = new QuerySaveRowsCommand();

                // Draft Ken Griffey Jr.
                saveCmd.addCommand(new Command(CommandType.Insert, schemaName, queryName, List.of(Map.of("FirstName", "Ken", "LastName", "Griffey Jr.", "JerseyNumber", 24, "Team", "Seattle Mariners"))));

                // Trade for Jay Buhner
                Command tradeJayBuhnerCommand = new Command(CommandType.Update, schemaName, queryName, List.of(
                    Map.of("JerseyNumber", 19, "Team", "Seattle Mariners"),
                    Map.of("JerseyNumber", 44, "Team", "New York Yankees")
                ));
                tradeJayBuhnerCommand.setAuditBehavior(SaveRowsCommand.AuditBehavior.DETAILED);
                tradeJayBuhnerCommand.setAuditUserComment("Traded Jay Buhner for Ken Phelps on July 21, 1988");
                saveCmd.addCommand(tradeJayBuhnerCommand);

                // Alvin Davis retires
                saveCmd.addCommand(new Command(CommandType.Delete, schemaName, queryName, List.of(Map.of("JerseyNumber", 21))));

                QuerySaveRowsResponse response = saveCmd.execute(conn, folderPath);
                System.out.printf("Executed saveRows command with %d errors and %d results%n", response.getErrorCount(), response.getResults().size());
            }
        }
        catch (Exception e)
        {
            System.out.printf("Error: %s%n", e.getMessage());
            System.out.printf("Type: %s%n", e.getClass().getName());
            System.out.println("Stack trace:");
            for (StackTraceElement element : e.getStackTrace())
                System.out.printf("    at %s%n", element.toString());
        }
        finally
        {
            new DeleteContainerCommand().execute(conn, folderPath);
        }
    }
}
