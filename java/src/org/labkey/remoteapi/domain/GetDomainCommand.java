package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

import java.util.HashMap;
import java.util.Map;

public class GetDomainCommand extends Command<DomainResponse>
{
    private String _schemaName;
    private String _queryName;
    private Long _domainId;

    public GetDomainCommand(String schemaName, String queryName)
    {
        super("property", "getDomain");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public GetDomainCommand(Long domainId)
    {
        super("property", "getDomain");
        _domainId = domainId;
    }

    public GetDomainCommand(Command<DomainResponse> source)
    {
        super(source);
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();

        if (_schemaName != null && _queryName != null)
        {
            params.put("schemaName", _schemaName);
            params.put("queryName", _queryName);
        }
        else if (_domainId != null)
        {
            params.put("domainId", _domainId);
        }
        return params;
    }

    @Override
    protected DomainResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new DomainResponse(text, status, contentType, json, this);
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    public String getQueryName()
    {
        return _queryName;
    }

    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    public Long getDomainId()
    {
        return _domainId;
    }

    public void setDomainId(Long domainId)
    {
        _domainId = domainId;
    }
}
