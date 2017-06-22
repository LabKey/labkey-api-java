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

import org.labkey.remoteapi.CommandResponse;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

/*
* User: Dave
* Date: Oct 21, 2008
* Time: 3:06:00 PM
*/

/**
 * Represents the response from a GetQueriesCommand.
 */
public class GetQueriesResponse extends CommandResponse
{
    public GetQueriesResponse(String text, int statusCode, String contentType, JSONObject json, GetQueriesCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     * Returns the schema name the queries in this response belong to.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return getProperty("schemaName");
    }

    /**
     * Returns the list of available query names in the given schema.
     * @return List of query names.
     */
    public List<String> getQueryNames()
    {
        List<Map<String,Object>> queries = getProperty("queries");
        if(null == queries)
            return Collections.emptyList();

        ArrayList<String> queryNames = new ArrayList<String>();
        for(Map<String,Object> query : queries)
        {
            if(query.containsKey("name"))
                queryNames.add((String)query.get("name"));
        }
        
        return queryNames;
    }

    /**
     * @param queryName The query name to find.
     * @return true if the query of interest is known to be a custom LabKey SQL query, false otherwise.
     */
    public boolean isUserDefined(String queryName)
    {
        Map<String, Object> query = getQuery(queryName);
        return query != null && query.containsKey("isUserDefined") && ((Boolean)query.get("isUserDefined")).booleanValue();
    }

    private Map<String, Object> getQuery(String queryName)
    {
        List<Map<String,Object>> queries = getProperty("queries");
        if(null == queries)
            return null;

        for(Map<String,Object> query : queries)
        {
            if(queryName.equals(query.get("name")))
            {
                return query;
            }
        }
        return null;
    }

    /**
     * Returns the list of column names available in the given query name. Note
     * that if the command was set to not include column information, this will return
     * an empty list.
     * @param queryName The query name to find.
     * @return The list of columns available within that query, or an empty list if not found.
     */
    @SuppressWarnings("unchecked")
    public List<String> getColumnNames(String queryName)
    {
        if(null == queryName)
            throw new IllegalArgumentException("queryName parameter was null!");

        Map<String, Object> query = getQuery(queryName);
        if (query != null && query.containsKey("columns"))
        {
            List<String> colNames = new ArrayList<String>();
            List<Map<String,Object>> cols = (List<Map<String,Object>>)query.get("columns");
            if(null != cols)
            {
                for(Map<String,Object> col : cols)
                    colNames.add((String)col.get("name"));
            }

            return colNames;
        }
        return Collections.emptyList();
    }
}