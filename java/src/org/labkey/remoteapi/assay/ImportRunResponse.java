package org.labkey.remoteapi.assay;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: kevink
 * Date: 9/12/12
 */
public class ImportRunResponse extends CommandResponse
{
    private String _successURL;
    private int _assayId;
    private int _batchId;
    private int _runId;

    public ImportRunResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
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
