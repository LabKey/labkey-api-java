/*
 * Copyright (c) 2009-2013 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

/**
 * Deletes existing users based on userIds
 * If it executed without exception, the users have been deleted
 */
public class DeleteUserCommand extends PostCommand<CommandResponse>
{
    private int _userId;

    public DeleteUserCommand(int userId)
    {
        super("security", "deleteUser");
        _userId = userId;
    }

    public DeleteUserCommand(DeleteUserCommand source)
    {
        super(source);
        _userId = source.getUserId();
    }

    public int getUserId()
    {
        return _userId;
    }

    public void setUserId(int userId)
    {
        _userId = userId;
    }

    @Override
    public PostCommand copy()
    {
        return new DeleteUserCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        assert _userId > 0 : "You didn't set the userId!";
        JSONObject obj = new JSONObject();
        obj.put("id", _userId);
        return obj;
    }
}
