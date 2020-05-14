package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;

import java.io.IOException;

/**
 * For site-admins or project-admins only, start impersonating a user.
 *
 * Admins may impersonate other users to perform actions on their behalf.
 * Site users may impersonate any user in any project. Project admins must
 * execute this command in a project in which they have admin permission
 * and may impersonate any user that has access to the project.
 *
 * To finish an impersonation session use either {@link LogoutCommand} to
 * log the original user out or use {@link StopImpersonatingCommand} to stop
 * impersonating while keeping the original user logged in.
 */
public class ImpersonateUserCommand extends PostCommand<CommandResponse>
{
    public ImpersonateUserCommand(int userId)
    {
        super("user", "impersonateUser.api");
        getParameters().put("userId", userId);
    }

    public ImpersonateUserCommand(String email)
    {
        super("user", "impersonateUser.api");
        getParameters().put("email", email);
    }
}
