package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

public class DomainResponse extends CommandResponse
{
    private final Domain _domain;

    public DomainResponse(String text, int statusCode, String contentType, JSONObject json, Command<DomainResponse> sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _domain = new Domain(json);
    }

    public Domain getDomain()
    {
        return _domain;
    }
}
