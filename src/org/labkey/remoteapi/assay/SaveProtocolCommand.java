package org.labkey.remoteapi.assay;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

public class SaveProtocolCommand extends PostCommand<ProtocolResponse>
{
    private final Protocol _protocol;

    public SaveProtocolCommand(Protocol protocol)
    {
        super("assay", "saveProtocol");
        _protocol = protocol;
    }

    @Override
    protected ProtocolResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ProtocolResponse(text, status, contentType, json);
    }

    @Override
    public JSONObject getJsonObject()
    {
        return _protocol.toJSONObject();
    }

    @Override
    public double getRequiredVersion()
    {
        return -1;
    }
}
