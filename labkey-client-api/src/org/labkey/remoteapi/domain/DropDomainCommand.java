package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

/**
 * Command to delete a domain.
 */
public class DropDomainCommand extends PostCommand<CommandResponse>
{
    private String _schemaName;
    private String _queryName;

    /**
     * Instantiate command to delete the specified domain.
     * @param schemaName parent schema of the domain to delete
     * @param queryName name of the domain to delete
     */
    public DropDomainCommand(String schemaName, String queryName)
    {
        super("property", "deleteDomain");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    /**
     * @return Name of the domain to delete
     */
    public String getQueryName()
    {
        return _queryName;
    }

    /**
     * Set the target domain's name
     * @param queryName Name of the domain to delete
     */
    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    /**
     * @return parent schema of the domain to delete
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Set the target domain's schema name
     * @param schemaName parent schema of the domain to delete
     */
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
