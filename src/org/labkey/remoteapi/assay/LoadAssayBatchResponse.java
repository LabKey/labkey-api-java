package org.labkey.remoteapi.assay;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

public class LoadAssayBatchResponse extends CommandResponse
{

    private Batch _batch;
    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text               The response text
     * @param statusCode         The HTTP status code
     * @param contentType        The response content type
     * @param json               The parsed JSONObject (or null if JSON was not returned)
     */
    public LoadAssayBatchResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _batch = new Batch(json.getJSONObject("batch"));
    }

    public Batch getBatch()
    {
        return _batch;
    }

    public void setBatch(Batch batch)
    {
        _batch = batch;
    }
}
