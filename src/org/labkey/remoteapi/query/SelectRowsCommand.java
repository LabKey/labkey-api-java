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

import java.util.ArrayList;
import java.util.List;

/**
 * Command for selecting data from any LabKey schema/query exposed on the
 * server identified by the Connection object supplied to the
 * {@link #execute(org.labkey.remoteapi.Connection, String)} method.
 * <p>
 * All data exposed from a LabKey Server is organized into a set of queries
 * contained in a set of schemas. A schema is simply a group of queries, identified
 * by a name (e.g., 'lists' or 'study'). A query is a particular table or view within
 * that schema (e.g., 'People' or 'Peptides'). For purposes of selecting, a query
 * may be either a base table or a view that joins data between related tables.
 * </p>
 * To view the schemas and queries exposed in a given folder, add a Query web part
 * to your portal page and choose the option "Show the list of tables in this schema"
 * in the part configuration page. Alternatively, if it is exposed, click on the Query
 * tab across the top of the main part of the page.
 * <p>Example:</p>
 * <pre>
 * <code>
 *     Connection cn = new Connection("https://www.labkey.org");
 *     SelectRowsCommand cmd = new SelectRowsCommand("study", "Physical Exam");
 *     SelectRowsResponse response = cmd.execute(cn, "Home/Study/demo");
 *     for(Map&lt;String, Object&gt; row : response.getRows())
 *     {
 *         System.out.println(row.get("ParticipantId") + " weighs " + row.get("APXwtkg"));
 *     }
 * </code>
 * </pre>
 */
public class SelectRowsCommand extends BaseQueryCommand<SelectRowsResponse> implements BaseSelect
{
    private String _schemaName;
    private String _queryName;
    private String _viewName;
    private List<String> _columns = new ArrayList<>();

    /**
     * Constructs a new SelectRowsCommand for the given schema
     * and query name.
     * @param schemaName The schema name.
     * @param queryName The query name.
     */
    public SelectRowsCommand(String schemaName, String queryName)
    {
        super("query", "selectRows");
        assert null != schemaName;
        assert null != queryName;
        _schemaName = schemaName;
        _queryName = queryName;
    }

    /**
     * Set to true to request the extended response format, which includes URLs, Missing-Value indicators and
     * display values in addition to the raw values. Use the getRowset() method on the response to iterate over the returned
     * rows in a format-neutral manner.
     * @param extendedFormat True to get the extended format.
     */
    public void setExtendedFormat(boolean extendedFormat)
    {
        setRequiredVersion(extendedFormat ? 9.1 : 8.3);
    }

    /**
     * Returns whether the extended format will be requested. See setExtendedFormat() for details.
     * @return true if extended format will be requested.
     */
    public boolean isExtendedFormat()
    {
        return getRequiredVersion() == 9.1;
    }

    /**
     * Returns the current schema name this command will query.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Sets the schema name this command will query.
     * @param schemaName The new schema name.
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    /**
     * Returns the query this command will request.
     * @return The current query name.
     */
    public String getQueryName()
    {
        return _queryName;
    }

    /**
     * Sets the query name this command will request.
     * @param queryName The new query name.
     */
    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    /**
     * Returns the current saved view name this command will request.
     * @return The view name, or null if the default view will be requested.
     */
    public String getViewName()
    {
        return _viewName;
    }

    /**
     * Sets a particular saved view to request. Users may create specific views
     * of any given query and save them under a name. Calling this method with a
     * particular name will cause the command to request the saved view instead
     * of the query's default view.
     * @param viewName The view name, or null to request the default view.
     */
    public void setViewName(String viewName)
    {
        _viewName = viewName;
    }

    /**
     * Returns the explicit column list to be requested, or null if the default
     * set of columns will be requested.
     * @return The column list or null.
     */
    public List<String> getColumns()
    {
        return _columns;
    }

    /**
     * Sets an explicit list of columns to request. To refer to columns in a related table, use the syntax
     * <i>foreign-key-column</i>/<i>related-column</i> (e.g., 'RelatedPeptide/Protein');
     * The special column name '*' can be used to request all columns in the target query.
     * @param columns The explicit column list, or null to request the default set of columns.
     */
    public void setColumns(List<String> columns)
    {
        _columns = columns;
    }

    /**
     * Overridden to create a SelectRowsResponse object.
     * @param text The response text
     * @param status The HTTP status code
     * @param contentType The Content-Type header value
     * @param json The parsed JSONObject (or null if JSON was not returned)
     * @return A SelectRowsResponse object
     */
    @Override
    protected SelectRowsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SelectRowsResponse(text, status, contentType, json, this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = super.getJsonObject();

        json.put("schemaName", getSchemaName());
        json.put("query.queryName", getQueryName());

        if (null != getViewName())
            json.put("query.viewName", getViewName());

        if (null != getColumns() && getColumns().size() > 0)
        {
            StringBuilder collist = new StringBuilder();
            String sep = "";
            for (String col : getColumns())
            {
                collist.append(sep);
                collist.append(col);
                sep = ",";
            }
            json.put("query.columns", collist);
        }

        return json;
    }
}
