/*
 * Copyright (c) 2009-2010 LabKey Corporation
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
import org.json.simple.JSONObject;

/*
* User: dave
* Date: Sep 28, 2009
* Time: 2:58:33 PM
*/

/**
 * Deletes the existing group corresponding to the group id.
 * Note that the response to the command contains nothing meaningful.
 * If it executed without exception, the group has been deleted.
 */
public class DeleteGroupCommand extends PostCommand<CommandResponse>
{
    private int _groupId = -1;

    public DeleteGroupCommand(int groupId)
    {
        super("security", "deleteGroup");
        _groupId = groupId;
    }

    public DeleteGroupCommand(DeleteGroupCommand source)
    {
        super(source);
        _groupId = source.getGroupId();
    }

    public int getGroupId()
    {
        return _groupId;
    }

    public void setGroupId(int groupId)
    {
        _groupId = groupId;
    }

    @Override
    public PostCommand copy()
    {
        return new DeleteGroupCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        assert _groupId >= 0 : "You didn't set the group id!";
        JSONObject obj = new JSONObject();
        obj.put("id", _groupId);
        return obj;
    }
}
