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
        return new LoadAssayBatchResponse(text, status, contentType, json, this);
    }
}
