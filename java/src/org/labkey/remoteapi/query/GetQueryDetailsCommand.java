/*
 * Copyright (c) 2008-2017 LabKey Corporation
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
import org.labkey.remoteapi.Command;

import java.util.HashMap;
import java.util.Map;

/*
* User: Dave
* Date: Oct 21, 2008
* Time: 3:05:51 PM
*/

/**
 *  Command for obtaining the list of queries available within a given schema.
 */
public class GetQueryDetailsCommand extends Command<GetQueryDetailsResponse>
{
    private String _schemaName;
    private String _queryName;

    /**
     * Constructs the command given a particular schema name.
     * @param schemaName The schema name.
     * @param queryName The name of the query (e.g., table) for the corresponding schema.
     */
    public GetQueryDetailsCommand(String schemaName, String queryName)
    {
        super("query", "getQueryDetails");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public GetQueryDetailsCommand(GetQueryDetailsCommand source)
    {
        super(source);
        _schemaName = source._schemaName;
        _queryName = source._queryName;
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

    public Map<String, Object> getParameters()
    {
        assert null != getSchemaName() : "You must set the schema name before executing the GetQueryDetailsCommand!";
        assert null != getQueryName() : "You must set the query name before executing the GetQueryDetailsCommand!";

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("schemaName", getSchemaName());
        params.put("queryName", getQueryName());

        return params;
    }

    protected GetQueryDetailsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetQueryDetailsResponse(text, status, contentType, json, this.copy());
    }

    @Override
    public GetQueryDetailsCommand copy()
    {
        return new GetQueryDetailsCommand(this);
    }
}