/*
 * Copyright (c) 2008-2010 LabKey Corporation
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
import org.labkey.remoteapi.Command;

import java.util.HashMap;
import java.util.Map;

/**
 *  Command for obtaining the list of queries available within a given schema.
 */
public class GetQueriesCommand extends Command<GetQueriesResponse>
{
    private String _schemaName;
    private boolean _includeColumns = true;
    private boolean _includeTitle = true;
    private boolean _includeUserQueries = true;
    private boolean _includeViewDataUrl = true;

    /**
     * Constructs the command given a particular schema name.
     * @param schemaName The schema name.
     */
    public GetQueriesCommand(String schemaName)
    {
        super("query", "getQueries");
        _schemaName = schemaName;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    public boolean isIncludeColumns()
    {
        return _includeColumns;
    }

    /**
     * Pass false to omit information about the columns within each query. By default,
     * column information is included.
     * @param includeColumns 'false' to omit column information.
     */
    public void setIncludeColumns(boolean includeColumns)
    {
        _includeColumns = includeColumns;
    }

    public boolean isIncludeTitle()
    {
        return _includeTitle;
    }

    /**
     * Pass false to omit custom query titles. Titles will be returned, but their values will be identical to names.
     * Default is true.
     * @param includeTitle 'false' to omit custom query titles.
     */
    public void setIncludeTitle(boolean includeTitle)
    {
        _includeTitle = includeTitle;
    }

    public boolean isIncludeUserQueries()
    {
        return _includeUserQueries;
    }

    /**
     * Pass false to this method to omit user-defined queries from the results. By default,
     * user-defined queries are included.
     * @param includeUserQueries Set to 'false' to omit user-defined queries.
     */
    public void setIncludeUserQueries(boolean includeUserQueries)
    {
        _includeUserQueries = includeUserQueries;
    }

    public boolean isIncludeViewDataUrl()
    {
        return _includeViewDataUrl;
    }

    /**
     * Pass false to omit view data URLs from the results. Default is true.
     * @param includeViewDataUrl Set to 'false' to omit view data URLs.
     */
    public void setIncludeViewDataUrl(boolean includeViewDataUrl)
    {
        _includeViewDataUrl = includeViewDataUrl;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        assert null != getSchemaName() : "You must set the schema name before executing the GetQueriesCommand!";

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", getSchemaName());

        if (!isIncludeColumns())
            params.put("includeColumns", isIncludeColumns());
        if (!isIncludeTitle())
            params.put("includeTitle", isIncludeTitle());
        if (!isIncludeUserQueries())
            params.put("includeUserQueries", isIncludeUserQueries());
        if (!isIncludeViewDataUrl())
            params.put("includeViewDataUrl", isIncludeViewDataUrl());

        return params;
    }

    @Override
    protected GetQueriesResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetQueriesResponse(text, status, contentType, json, this);
    }
}
