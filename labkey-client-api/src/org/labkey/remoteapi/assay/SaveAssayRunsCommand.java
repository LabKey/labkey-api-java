package org.labkey.remoteapi.assay;


import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves runs on the server.
 */
public class SaveAssayRunsCommand extends PostCommand<SaveAssayRunsResponse>
{
    private List<Run> _runs;
    private String _protocolName;

    /**
     * @param protocolName name of the protocol to use
     * @param runs the runs to be saved
     */
    public SaveAssayRunsCommand(String protocolName, List<Run> runs)
    {
        super("assay", "saveAssayRuns");
        _runs = runs;
        _protocolName = protocolName;
    }

    public SaveAssayRunsCommand(SaveAssayRunsCommand source)
    {
        super(source);
        _runs = new ArrayList<>(source._runs);
        _protocolName = source._protocolName;
    }

    @Override
    public SaveAssayRunsCommand copy()
    {
        return new SaveAssayRunsCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.put(run.toJSONObject());
        }
        result.put("runs", runs);
        result.put("protocolName", getProtocolName());
        return result;
    }

    @Override
    protected SaveAssayRunsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveAssayRunsResponse(text, status, contentType, json, this);
    }

    public List<Run> getRuns()
    {
        return _runs;
    }

    public void setRuns(List<Run> runs)
    {
        _runs = runs;
    }

    public String getProtocolName()
    {
        return _protocolName;
    }

    public void setProtocolName(String protocolName)
    {
        _protocolName = protocolName;
    }
}
