package org.labkey.remoteapi.assay;


import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Command for obtaining information about a run in a particular folder.
 */
public class LoadAssayRunCommand extends PostCommand<LoadAssayRunResponse>
{
    private String _lsid;

    public LoadAssayRunCommand(LoadAssayRunCommand source)
    {
        super(source);
        _lsid = source.getLsid();
    }

    public LoadAssayRunCommand(String lsid)
    {
        super("experiment", "loadAssayRun");
        _lsid = lsid;
    }

    public String getLsid()
    {
        return _lsid;
    }

    public void setLsid(String lsid)
    {
        _lsid = lsid;
    }

    @Override
    public LoadAssayRunCommand copy()
    {
        return new LoadAssayRunCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        result.put("lsid", _lsid);
        return result;
    }

    @Override
    protected LoadAssayRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new LoadAssayRunResponse(text, status, contentType, json, this);
    }
}
