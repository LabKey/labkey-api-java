/*
 * Copyright (c) 2020-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.security;

import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.util.HashMap;
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
    private final Map<String, Object> _parameters = new HashMap<>();

    public ImpersonateUserCommand(int userId)
    {
        super("user", "impersonateUser.api");
        _parameters.put("userId", userId);
    }

    public ImpersonateUserCommand(String email)
    {
        super("user", "impersonateUser.api");
        _parameters.put("email", email);
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        return new HashMap<>(_parameters); // Return a copy
    }
}
