package org.labkey.remoteapi.security;

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * Ends the current HTTP session, if any is active.
 */
public class LogoutCommand extends Command<CommandResponse>
{
    public LogoutCommand()
    {
        super("login", "logout");
    }
}
