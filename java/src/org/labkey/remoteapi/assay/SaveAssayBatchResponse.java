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
