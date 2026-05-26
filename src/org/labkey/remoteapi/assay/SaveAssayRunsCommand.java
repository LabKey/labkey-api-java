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


import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.List;

/**
 * Saves runs on the server.
 */
public class SaveAssayRunsCommand extends PostCommand<SaveAssayRunsResponse>
{
    private List<Run> _runs;
    private String _protocolName;

    /**
     * @param protocolName name of the protocol to use
     * @param runs the runs to be saved
     */
    public SaveAssayRunsCommand(String protocolName, List<Run> runs)
    {
        super("assay", "saveAssayRuns");
        _runs = runs;
        _protocolName = protocolName;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.put(run.toJSONObject());
        }
        result.put("runs", runs);
        result.put("protocolName", getProtocolName());
        return result;
    }

    @Override
    protected SaveAssayRunsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveAssayRunsResponse(text, status, contentType, json);
    }

    public List<Run> getRuns()
    {
        return _runs;
    }

    public void setRuns(List<Run> runs)
    {
        _runs = runs;
    }

    public String getProtocolName()
    {
        return _protocolName;
    }

    public void setProtocolName(String protocolName)
    {
        _protocolName = protocolName;
    }
}
