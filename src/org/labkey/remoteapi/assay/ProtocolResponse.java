package org.labkey.remoteapi.assay;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

public class ProtocolResponse extends CommandResponse
{
    private final Protocol _protocol;

    public ProtocolResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _protocol = new Protocol(json.getJSONObject("data"));
    }

    public Protocol getProtocol()
    {
        return _protocol;
    }
}
