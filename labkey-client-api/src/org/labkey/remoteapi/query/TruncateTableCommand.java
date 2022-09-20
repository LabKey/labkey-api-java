/*
 * Copyright (c) 2008-2009 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.query;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Command for truncating a table from a read-write schema. The user associated
 * with the connection used when executing this command must have
 * permission to delete the data.
 */
public class TruncateTableCommand extends PostCommand<TruncateTableResponse>
{
    private String _schemaName;
    private String _queryName;

    /**
     * Constructs a TruncateTableCommand for the given schemaName and queryName.
     * @param schemaName The schemaName
     * @param queryName The queryName
     */
    public TruncateTableCommand(String schemaName, String queryName)
    {
        super("query", "truncateTable");
        assert null != schemaName;
        assert null != queryName;
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public TruncateTableCommand(TruncateTableCommand source)
    {
        super(source);
    }

    /**
     * Returns the schema name.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Sets the schema name
     * @param schemaName The new schema name.
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    /**
     * Returns the query name
     * @return the query name.
     */
    public String getQueryName()
    {
        return _queryName;
    }

    /**
     * Sets a new query name to update
     * @param queryName the query name.
     */
    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    @Override
    public TruncateTableCommand copy()
    {
        return new TruncateTableCommand(this);
    }

    @Override
    protected TruncateTableResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new TruncateTableResponse(text, status, contentType, json, copy());
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