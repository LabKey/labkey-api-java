package org.labkey.remoteapi.assay;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.HasRequiredVersion;

public class ProtocolResponse extends CommandResponse
{
    private final Protocol _protocol;

    public ProtocolResponse(String text, int statusCode, String contentType, JSONObject json, HasRequiredVersion hasRequiredVersion)
    {
        super(text, statusCode, contentType, json, hasRequiredVersion);
        _protocol = new Protocol((JSONObject) json.get("data"));
    }

    public Protocol getProtocol()
    {
        return _protocol;
    }
}
