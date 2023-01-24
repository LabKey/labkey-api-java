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

import org.json.JSONObject;
import org.labkey.remoteapi.GetCommand;

import java.util.Map;

public class GetContainersCommand extends GetCommand<GetContainersResponse>
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

    @Override
    protected GetContainersResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetContainersResponse(text, status, contentType, json);
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        Map<String, Object> params = super.createParameterMap();
        params.put("includeSubfolders", _includeSubfolders);

        return params;
    }
}
