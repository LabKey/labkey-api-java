/*
 * Copyright (c) 2008-2010 LabKey Corporation
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

public class GetGroupPermsCommand extends GetCommand<GetGroupPermsResponse>
{
    private boolean _includeSubfolders = false;

    public GetGroupPermsCommand()
    {
        super("security", "getGroupPerms");
    }

    /**
     * Returns whether the command will recurse down the subfolders
     * of the folder in which the command is executed.
     * @return true or false (default is false).
     */
    public boolean isIncludeSubfolders()
    {
        return _includeSubfolders;
    }

    /**
     * Sets whether this command should recurse down the subfolders
     * of the folder in which the command is executed.
     * @param includeSubfolders true to recurse
     */
    public void setIncludeSubfolders(boolean includeSubfolders)
    {
        _includeSubfolders = includeSubfolders;
    }

    @Override
    protected GetGroupPermsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetGroupPermsResponse(text, status, contentType, json);
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        Map<String, Object> params = super.createParameterMap();
        params.put("includeSubfolders", isIncludeSubfolders());

        return params;
    }
}
