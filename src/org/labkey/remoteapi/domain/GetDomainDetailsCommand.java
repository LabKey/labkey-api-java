package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;

import java.util.HashMap;
import java.util.Map;

public class GetDomainDetailsCommand extends Command<DomainDetailsResponse>
{
    private String _schemaName;
    private String _queryName;
    private Long _domainId;

    public GetDomainDetailsCommand(String schemaName, String queryName)
    {
        super("property", "getDomainDetails");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public GetDomainDetailsCommand(Long domainId)
    {
        super("property", "getDomainDetails");
        _domainId = domainId;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = super.getParameters();

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
    protected DomainDetailsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new DomainDetailsResponse(text, status, contentType, json, this);
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
