package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.GetCommand;

import java.util.Map;

/**
 * @deprecated The associated server action was deprecated in LabKey Server 20.3
 * @see GetDomainDetailsCommand
 */
@Deprecated (since = "4.0.0")
public class GetDomainCommand extends GetCommand<DomainResponse>
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
