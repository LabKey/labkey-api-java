package org.labkey.remoteapi.security;

import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.util.Map;

/**
 * For site-admins or project-admins only, start impersonating a user.
 * <p>
 * Admins may impersonate other users to perform actions on their behalf.
 * Site users may impersonate any user in any project. Project admins must
 * execute this command in a project in which they have admin permission
 * and may impersonate any user that has access to the project.
 * <p>
 * To finish an impersonation session use either {@link LogoutCommand} to
 * log the original user out or use {@link StopImpersonatingCommand} to stop
 * impersonating while keeping the original user logged in.
 */
public class ImpersonateUserCommand extends PostCommand<CommandResponse>
{
    public ImpersonateUserCommand(int userId)
    {
        super("user", "impersonateUser.api");
        setParameters(Map.of("userId", userId));
    }

    public ImpersonateUserCommand(String email)
    {
        super("user", "impersonateUser.api");
        setParameters(Map.of("email", email));
    }
}
