package org.labkey.remoteapi.assay;


import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Command for obtaining information about a run in a particular folder.
 */
public class GetAssayRunCommand extends PostCommand<GetAssayRunResponse>
{
    private String _lsid;

    public GetAssayRunCommand(GetAssayRunCommand source)
    {
        super(source);
        _lsid = source.getLsid();
    }

    public GetAssayRunCommand(String lsid)
    {
        super("assay", "getAssayRun");
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
    public GetAssayRunCommand copy()
    {
        return new GetAssayRunCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        result.put("lsid", _lsid);
        return result;
    }

    @Override
    protected GetAssayRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetAssayRunResponse(text, status, contentType, json, this);
    }
}
