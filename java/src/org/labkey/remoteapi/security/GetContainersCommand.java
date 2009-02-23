/*
 * Copyright (c) 2008 LabKey Corporation
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

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.CommandException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/*
* User: Dave
* Date: Oct 27, 2008
* Time: 1:26:05 PM
*/
public class GetContainersCommand extends Command
{
    private boolean _includeSubfolders = false;

    public GetContainersCommand()
    {
        super("project", "getContainers");
    }

    public boolean isIncludeSubfolders()
    {
        return _includeSubfolders;
    }

    public void setIncludeSubfolders(boolean includeSubfolders)
    {
        _includeSubfolders = includeSubfolders;
    }

    protected CommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetContainersResponse(text, status, contentType, json, getRequiredVersion());
    }

    @SuppressWarnings("unchecked")
    public GetContainersResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (GetContainersResponse)super.execute(connection, folderPath);
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("includeSubfolders", _includeSubfolders);
        return params;
    }
}