package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.ApiVersionException;

import java.io.IOException;

/**
 * User: adam
 * Date: Feb 23, 2009
 * Time: 9:37:55 AM
 */
public class SASBaseSelectCommand
{
    // Share the version handling between SASSelectRowsCommand and SASExecuteSqlCommand 
    protected static SelectRowsResponse execute(Command command, SASConnection cn, String folderPath) throws CommandException, IOException
    {
        // "Require" 9.1 first, which is needed for QC, but fall back to 8.3
        command.setRequiredVersion(9.1);

        try
        {
            return (SelectRowsResponse)command.execute(cn, folderPath);
        }
        catch (ApiVersionException ave)
        {
            // Drop through and try again, requiring 8.3
        }

        command.setRequiredVersion(8.3);
        return (SelectRowsResponse)command.execute(cn, folderPath);
    }
}
