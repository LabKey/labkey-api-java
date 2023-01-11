/*
 * Copyright (c) 2009-2019 LabKey Corporation
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
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for AddGroupMembersCommand and RemoveGroupMembersCommand
 */
public abstract class GroupMembersCommand extends PostCommand<CommandResponse>
{
    private int _groupId;
    private final List<Integer> _principals = new ArrayList<>();

    protected GroupMembersCommand(String actionName, int groupId)
    {
        super("security", actionName);
        _groupId = groupId;
    }

    protected GroupMembersCommand(GroupMembersCommand source)
    {
        super(source);
        _groupId = source.getGroupId();
        _principals.addAll(source.getPrincipals());
    }

    public List<Integer> getPrincipals()
    {
        return _principals;
    }

    public void addPrincipalId(int... ids)
    {
        for (int id : ids)
        {
            _principals.add(Integer.valueOf(id));
        }
    }

    public void addPrincipalId(List<Integer> ids)
    {
        for (int id : ids)
        {
            _principals.add(Integer.valueOf(id));
        }
    }

    public void clearPrincipalIds()
    {
        _principals.clear();
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
    public JSONObject getJsonObject()
    {
        assert getGroupId() != 0 : "forgot to set group id!";
        assert _principals.size() > 0 : "forgot to add principal id(s)!";
        JSONObject obj = new JSONObject();
        obj.put("groupId", getGroupId());
        obj.put("principalIds", _principals);
        return obj;
    }
}