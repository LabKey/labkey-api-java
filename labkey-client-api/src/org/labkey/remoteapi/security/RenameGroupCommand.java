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

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Renames an existing group.
 */
public class RenameGroupCommand extends PostCommand<RenameGroupResponse>
{
    private int _groupId;
    private String _newName;

    public RenameGroupCommand(int groupId, String newName)
    {
        super("security", "renameGroup");
        _groupId = groupId;
        _newName = newName;
    }

    public int getGroupId()
    {
        return _groupId;
    }

    public void setGroupId(int groupId)
    {
        _groupId = groupId;
    }

    public String getNewName()
    {
        return _newName;
    }

    public void setNewName(String newName)
    {
        _newName = newName;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject obj = new JSONObject();
        obj.put("id", getGroupId());
        obj.put("newName", getNewName());
        return obj;
    }

    @Override
    protected RenameGroupResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new RenameGroupResponse(text, status, contentType, json, this);
    }
}
