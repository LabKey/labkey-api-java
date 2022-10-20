package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

public class DropDomainCommand extends PostCommand<CommandResponse>
{
    private String _schemaName;
    private String _queryName;

    public DropDomainCommand(String schemaName, String queryName)
    {
        super("property", "deleteDomain");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public String getQueryName()
    {
        return _queryName;
    }

    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("queryName", getQueryName());

        return json;
    }
}
