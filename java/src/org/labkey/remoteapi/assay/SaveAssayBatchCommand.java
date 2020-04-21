/*
 * Copyright (c) 2010-2018 LabKey Corporation
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
import org.labkey.remoteapi.PostCommand;

/**
 * Saves an assay batch on the server. If no batch or run ids are specified, new ones will be inserted. If they are
 * specified, existing ones will be updated.
 * User: jeckels
 * Date: Apr 28, 2010
 */
public class SaveAssayBatchCommand extends PostCommand<SaveAssayBatchResponse>
{
    public static final String SAMPLE_DERIVATION_PROTOCOL = "Sample Derivation Protocol";       // protocol name that can be used to create non-assay backed runs

    private Batch _batch = new Batch();
    private int _assayId;
    private String _protocolName;

    /** @param assayId the id of the assay definition on the web server */
    public SaveAssayBatchCommand(int assayId)
    {
        super("assay", "saveAssayBatch");
        _assayId = assayId;
    }

    /**
     * @param assayId the id of the assay definition on the web server
     * @param batch the batch object to be saved
     */
    public SaveAssayBatchCommand(int assayId, Batch batch)
    {
        this(assayId);
        _batch = batch;
    }

    /**
     * @param protocolName the name of the protocol to use for non-assay backed runs
     * @param batch the batch object to be saved
     */
    public SaveAssayBatchCommand(String protocolName, Batch batch)
    {
        this(-1);
        _protocolName = protocolName;
        _batch = batch;
    }

    public SaveAssayBatchCommand(SaveAssayBatchCommand source)
    {
        super(source);

        _assayId = source._assayId;
        _protocolName = source._protocolName;
        _batch = source._batch;
    }

    @Override
    public SaveAssayBatchCommand copy()
    {
        return new SaveAssayBatchCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        if (_protocolName != null)
            result.put("protocolName", _protocolName);
        else
            result.put("assayId", _assayId);

        result.put("batch", _batch.toJSONObject());
        return result;
    }

    @Override
    protected SaveAssayBatchResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveAssayBatchResponse(text, status, contentType, json, this);
    }
}
