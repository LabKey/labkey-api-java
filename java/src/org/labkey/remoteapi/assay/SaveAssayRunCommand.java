package org.labkey.remoteapi.assay;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves runs on the server.
 */
public class SaveAssayRunCommand extends PostCommand<SaveAssayRunResponse>
{
    private List<Run> _runs;
    private String _protocolName;

    /**
     * @param runs the runs to be saved
     */
    public SaveAssayRunCommand(String protocolName, List<Run> runs)
    {
        super("assay", "saveAssayRun");
        _runs = runs;
        _protocolName = protocolName;
    }

    public SaveAssayRunCommand(SaveAssayRunCommand source)
    {
        super(source);
        _runs = source._runs;
    }

    @Override
    public PostCommand copy()
    {
        return new SaveAssayRunCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.add(run.toJSONObject());
        }
        result.put("runs", runs);
        result.put("protocolName", getProtocolName());
        return result;
    }

    @Override
    protected SaveAssayRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveAssayRunResponse(text, status, contentType, json, this);
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
