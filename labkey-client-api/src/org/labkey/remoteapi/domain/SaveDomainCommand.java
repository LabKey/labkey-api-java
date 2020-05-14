package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;

public class SaveDomainCommand extends AbstractDomainUpdateCommand
{
    private String _schemaName;
    private String _queryName;
    private Long _domainId;

    public SaveDomainCommand(String schemaName, String queryName)
    {
        super("property", "saveDomain");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public SaveDomainCommand(Long domainId)
    {
        super("property", "saveDomain");
        _domainId = domainId;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();

        if (_schemaName != null && _queryName != null)
        {
            result.put("schemaName", _schemaName);
            result.put("queryName", _queryName);
        }
        else if (_domainId != null)
        {
            result.put("domainId", _domainId);
        }
        return result;
    }
}
