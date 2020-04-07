package org.labkey.remoteapi.security;

import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

/**
 * Stop impersonating a user.
 */
public class StopImpersonatingCommand extends PostCommand<CommandResponse>
{
    public StopImpersonatingCommand()
    {
        super("login", "stopImpersonating.api");
    }
}
