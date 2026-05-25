/*
 * Copyright (c) 2019-2026 LabKey Corporation
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
package org.labkey.remoteapi.assay;


import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Command for obtaining information about a run in a particular folder.
 */
public class GetAssayRunCommand extends PostCommand<GetAssayRunResponse>
{
    private String _lsid;

    public GetAssayRunCommand(String lsid)
    {
        super("assay", "getAssayRun");
        _lsid = lsid;
    }

    public String getLsid()
    {
        return _lsid;
    }

    public void setLsid(String lsid)
    {
        _lsid = lsid;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        result.put("lsid", _lsid);
        return result;
    }

    @Override
    protected GetAssayRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetAssayRunResponse(text, status, contentType, json);
    }
}
