/*
 * Copyright (c) 2012 LabKey Corporation
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
import org.labkey.remoteapi.CommandResponse;

public class ImportRunResponse extends CommandResponse
{
    private final String _successURL;
    private final int _assayId;
    private final int _batchId;
    private final int _runId;

    public ImportRunResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _successURL = (String)json.get("successurl");
        _assayId = ((Number)json.get("assayId")).intValue();
        _batchId = ((Number)json.get("batchId")).intValue();
        _runId = ((Number)json.get("runId")).intValue();
    }

    public String getSuccessURL()
    {
        return _successURL;
    }

    public int getAssayId()
    {
        return _assayId;
    }

    public int getBatchId()
    {
        return _batchId;
    }

    public int getRunId()
    {
        return _runId;
    }

}
