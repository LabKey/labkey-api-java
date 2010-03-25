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

import org.labkey.remoteapi.Command;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.HashMap;

/*
* User: Dave
* Date: Oct 21, 2008
* Time: 3:05:51 PM
*/

/**
 *  Command for obtaining the list of queries available within a given schema.
 */
public class GetQueriesCommand extends Command<GetQueriesResponse>
{
    private String _schemaName;
    private boolean _includeUserQueries = true;
    private boolean _includeColumns = true;

    /**
     * Constructs the command given a particular schema name.
     * @param schemaName The schema name.
     */
    public GetQueriesCommand(String schemaName)
    {
        super("query", "getQueries");
        _schemaName = schemaName;
    }

    public GetQueriesCommand(GetQueriesCommand source)
    {
        super(source);
        _schemaName = source._schemaName;
        _includeUserQueries = source._includeUserQueries;
        _includeColumns = source._includeColumns;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    public boolean isIncludeUserQueries()
    {
        return _includeUserQueries;
    }

    /**
     * Pass false to this method to omit user-defined queries from the results. By default
     * user-defined queries are included.
     * @param includeUserQueries Set to false to omit user-defined queries.
     */
    public void setIncludeUserQueries(boolean includeUserQueries)
    {
        _includeUserQueries = includeUserQueries;
    }

    public boolean isIncludeColumns()
    {
        return _includeColumns;
    }

    /**
     * Pass false to omit information about the columns within each query. By default
     * column information is included.
     * @param includeColumns Set to false to omit column information.
     */
    public void setIncludeColumns(boolean includeColumns)
    {
        _includeColumns = includeColumns;
    }

    public Map<String, Object> getParameters()
    {
        assert null != getSchemaName() : "You must set the schema name before executing the GetQueriesCommand!";

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("schemaName", getSchemaName());

        if(!isIncludeColumns())
            params.put("includeColumns", isIncludeColumns());
        if(!isIncludeUserQueries())
            params.put("includeUserQueries", isIncludeUserQueries());
        
        return params;
    }

    protected GetQueriesResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetQueriesResponse(text, status, contentType, json, this.copy());
    }

    @Override
    public GetQueriesCommand copy()
    {
        return new GetQueriesCommand(this);
    }
}