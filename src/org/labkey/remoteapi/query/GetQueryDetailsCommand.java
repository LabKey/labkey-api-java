/*
 * Copyright (c) 2008-2018 LabKey Corporation
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

import org.json.JSONObject;
import org.labkey.remoteapi.GetCommand;

import java.util.Map;

/**
 *  Command for obtaining the list of queries available within a given schema.
 */
public class GetQueryDetailsCommand extends GetCommand<GetQueryDetailsResponse>
{
    private String _schemaName;
    private String _queryName;
    private final boolean _includeSuggestedQueryColumns;

    /**
     * Constructs the command given a particular schema name.
     * @param schemaName The schema name.
     * @param queryName The name of the query (e.g., table) for the corresponding schema.
     * @param includeSuggestedQueryColumns Default true. Include the auto suggested columns in the response, e.g., user defined queries always have the container column of their root table added.
     */
    public GetQueryDetailsCommand(String schemaName, String queryName, boolean includeSuggestedQueryColumns)
    {
        super("query", "getQueryDetails");
        _schemaName = schemaName;
        _queryName = queryName;
        _includeSuggestedQueryColumns = includeSuggestedQueryColumns;
    }

    /**
     * Constructs the command given a particular schema name.
     * @param schemaName The schema name.
     * @param queryName The name of the query (e.g., table) for the corresponding schema.
     */
    public GetQueryDetailsCommand(String schemaName, String queryName)
    {
        this(schemaName, queryName, true);
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

    @Override
    protected Map<String, Object> createParameterMap()
    {
        assert null != getSchemaName() : "You must set the schema name before executing the GetQueryDetailsCommand!";
        assert null != getQueryName() : "You must set the query name before executing the GetQueryDetailsCommand!";

        Map<String, Object> params = super.createParameterMap();
        params.put("schemaName", getSchemaName());
        params.put("queryName", getQueryName());
        params.put("includeSuggestedQueryColumns", _includeSuggestedQueryColumns);

        return params;
    }

    @Override
    protected GetQueryDetailsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetQueryDetailsResponse(text, status, contentType, json);
    }
}
