/*
 * Copyright (c) 2017 LabKey Corporation
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
package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: kevink
 * Date: 2/3/17
 */
public class ImportDataResponse extends CommandResponse
{
    private final Boolean _success;
    private final int _rowCount;
    private final String _jobId;

    public ImportDataResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _success = json.optBoolean("success");
        _rowCount = json.optInt("rowCount");
        _jobId = json.optString("jobId");
    }

    public Boolean getSuccess()
    {
        return _success;
    }

    public int getRowCount()
    {
        return _rowCount;
    }

    /**
     * When importing a file asynchronously, the jobId of the queued job is returned.
     * @see ImportDataCommand#setUseAsync(boolean)
     */
    public String getJobId()
    {
        return _jobId;
    }
}
