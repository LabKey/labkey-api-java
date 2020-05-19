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

import org.labkey.remoteapi.PostCommand;
import org.json.simple.JSONObject;

/*
* User: dave
* Date: Sep 28, 2009
* Time: 2:53:02 PM
*/

/**
 * Creates a new user group. When executed in a project or subfolder, it will create
 * the group at the project level. When executed at the root level, it will create
 * a new site group.
 */
public class CreateGroupCommand extends PostCommand<CreateGroupResponse>
{
    private String _name;

    public CreateGroupCommand(String name)
    {
        super("security", "createGroup");
        _name = name;
    }

    public CreateGroupCommand(CreateGroupCommand source)
    {
        super(source);
        _name = source.getName();
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    @Override
    protected CreateGroupResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new CreateGroupResponse(text, status, contentType, json, this);
    }

    @Override
    public PostCommand copy()
    {
        return new CreateGroupCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject obj = new JSONObject();
        obj.put("name", getName());
        return obj;
    }
}
