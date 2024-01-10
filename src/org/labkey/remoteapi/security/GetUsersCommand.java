/*
 * Copyright (c) 2010-2018 LabKey Corporation
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
import org.labkey.remoteapi.GetCommand;

import java.util.Map;

/**
 * Returns a list of users given selection criteria. This may be called by any logged-in user.
 */
public class GetUsersCommand extends GetCommand<GetUsersResponse>
{
    private int _groupId = -1;
    private String _name;
    private String _group;
    private boolean _includeInactive = false;

    public GetUsersCommand()
    {
        super("user", "getUsers");
    }

    /** @return the id of a project group for which you want the members */
    public int getGroupId()
    {
        return _groupId;
    }

    /** @param groupId the id of a project group for which you want the members (specify groupId or group, not both). */
    public void setGroupId(int groupId)
    {
        if (_group != null)
        {
            throw new IllegalStateException("Group and GroupId should not both be specified");
        }
        _groupId = groupId;
    }

    /** @return the name of a project group for which you want the members */
    public String getGroup()
    {
        return _group;
    }

    /** @param group the name of a project group for which you want the members (specify groupId or group, not both). */
    public void setGroup(String group)
    {
        if (_groupId != -1)
        {
            throw new IllegalStateException("Group and GroupId should not both be specified");
        }
        _group = group;
    }

    /** @return the first part of the username, useful for username completion */
    public String getName()
    {
        return _name;
    }

    /**
     * @param name The first part of the username, useful for username completion. If specified, only users whose
     * email address or display name starts with the value supplied will be returned.
     */
    public void setName(String name)
    {
        _name = name;
    }

    @Deprecated(forRemoval = true) // Remove in 7.0.0
    public Boolean getIncludeDeactivated()
    {
        return _includeInactive;
    }

    @Deprecated(forRemoval = true) // Remove in 7.0.0
    public void setIncludeDeactivated(Boolean includeInactive)
    {
        _includeInactive = (null != includeInactive && includeInactive);
    }

    /**
     * @return Flag to request inactive users as well
     */
    public boolean getIncludeInactive()
    {
        return _includeInactive;
    }

    public void setIncludeInactive(boolean includeInactive)
    {
        _includeInactive = includeInactive;
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        Map<String, Object> params = super.createParameterMap();
        if (_group != null)
        {
            params.put("group", _group);
        }
        if (_groupId != -1)
        {
            params.put("groupId", _groupId);
        }
        if (_name != null)
        {
            params.put("name", _name);
        }
        if (_includeInactive)
        {
            params.put("active", false);
        }
        return params;
    }

    @Override
    protected GetUsersResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetUsersResponse(text, status, contentType, json);
    }
}
