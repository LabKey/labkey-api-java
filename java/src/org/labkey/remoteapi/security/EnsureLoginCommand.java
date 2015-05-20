package org.labkey.remoteapi.security;

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * Simply ensures that the connection is associated with an authenticated user.
 * User: jeckels
 * Date: 5/19/2015
 */
public class EnsureLoginCommand extends Command<CommandResponse>
{
    public EnsureLoginCommand()
    {
        super("security", "ensureLogin");
    }
}
