package org.labkey.remoteapi.assay;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Saves an assay batch on the server. If no batch or run ids are specified, new ones will be inserted. If they are,
 * specified, existing ones will be updated.
 * User: jeckels
 * Date: Apr 28, 2010
 */
public class SaveAssayBatchCommand extends PostCommand<SaveAssayBatchResponse>
{
    private Batch _batch = new Batch();
    private int _assayId;

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

    @Override
    public SaveAssayBatchCommand copy()
    {
        return new SaveAssayBatchCommand(_assayId, _batch);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
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
