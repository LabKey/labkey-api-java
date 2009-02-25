/*
 * Copyright (c) 2008-2009 LabKey Corporation
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

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/*
* User: Dave
* Date: Sep 9, 2008
* Time: 2:12:46 PM
*/
public class GetGroupPermsCommand extends Command
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

    public GetGroupPermsResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (GetGroupPermsResponse)super.execute(connection, folderPath);
    }

    protected GetGroupPermsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetGroupPermsResponse(text, status, contentType, json, getRequiredVersion());
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("includeSubfolders", isIncludeSubfolders());
        return params;
    }
}