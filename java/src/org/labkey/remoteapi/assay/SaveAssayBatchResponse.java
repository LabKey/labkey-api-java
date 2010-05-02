/*
 * Copyright (c) 2010 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: jeckels
 * Date: Apr 28, 2010
 */
public class SaveAssayBatchResponse extends CommandResponse
{
    private Batch _batch;
    private int _assayId;

    public SaveAssayBatchResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _batch = new Batch((JSONObject)json.get("batch"));
        _assayId = ((Number)json.get("assayId")).intValue();
    }

    public Batch getBatch()
    {
        return _batch;
    }

    public int getAssayId()
    {
        return _assayId;
    }
}
