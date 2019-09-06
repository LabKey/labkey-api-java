package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

import java.util.List;
import java.util.Map;

public class DomainResponse extends CommandResponse
{
    private Domain _domain;

    public DomainResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _domain = new Domain(json);
    }

    public Domain getDomain()
    { 
        return _domain;
    }

    public List<Map<String, Object>> getColumns()
    {
        return (List<Map<String, Object>>) getParsedData().get("fields");
    }

    public Long getDomainId()
    {
        return (Long)getParsedData().get("domainId");
    }

    public String getDomainURI()
    {
        return (String)getParsedData().get("domainURI");
    }
}
