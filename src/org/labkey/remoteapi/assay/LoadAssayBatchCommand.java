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
 * Command for obtaining information about the current batch in a particular folder.
 */
public class LoadAssayBatchCommand extends PostCommand<LoadAssayBatchResponse>
{

    private String _protocolName;
    private int _batchId;
    private Batch _batch;

    public Batch getBatch()
    {
        return _batch;
    }

    public void setBatch(Batch batch)
    {
        _batch = batch;
    }

    public LoadAssayBatchCommand(String protocolName, int batchId)
    {
        super("assay", "getAssayBatch");
        _protocolName = protocolName;
        _batchId = batchId;
    }

    public String getProtocolName()
    {
        return _protocolName;
    }

    public void setProtocolName(String protocolName)
    {
        _protocolName = protocolName;
    }

    public int getBatchId()
    {
        return _batchId;
    }

    public void setBatchId(int batchId)
    {
        _batchId = batchId;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        result.put("protocolName", getProtocolName());
        result.put("batchId", getBatchId());
        result.put("batch", getBatch());
        return result;
    }

    @Override
    protected LoadAssayBatchResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new LoadAssayBatchResponse(text, status, contentType, json);
    }
}
