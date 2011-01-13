/*
 * Copyright (c) 2008-2011 LabKey Corporation
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
import org.labkey.remoteapi.Command;

import java.util.HashMap;
import java.util.Map;

/*
* User: Dave
* Date: Oct 27, 2008
* Time: 1:26:05 PM
*/
public class GetContainersCommand extends Command<GetContainersResponse>
{
    private boolean _includeSubfolders = false;

    public GetContainersCommand()
    {
        super("project", "getContainers");
    }

    public GetContainersCommand(GetContainersCommand source)
    {
        super(source);
        _includeSubfolders = source._includeSubfolders;
    }

    public boolean isIncludeSubfolders()
    {
        return _includeSubfolders;
    }

    public void setIncludeSubfolders(boolean includeSubfolders)
    {
        _includeSubfolders = includeSubfolders;
    }

    protected GetContainersResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetContainersResponse(text, status, contentType, json, this.copy());
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("includeSubfolders", _includeSubfolders);
        return params;
    }

    @Override
    public GetContainersCommand copy()
    {
        return new GetContainersCommand(this);
    }
}