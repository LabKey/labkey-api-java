/*
 * Copyright (c) 2008 LabKey Corporation
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
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 10:54:45 AM
*/

/**
 * Command for selecting data from any LabKey schema/query exposed on the
 * server identified by the Connection object supplied to the
 * {@link #execute(org.labkey.remoteapi.Connection, String)} method.
 * <p>
 * All data exposed from a LabKey Server is organized into a set of queries
 * contained in a set of schemas. A schema is simply a group of queries, identified
 * by a name (e.g., 'lists' or 'study'). A query is particular table or view within
 * that schema (e.g., 'People' or 'Peptides'). For purposes of selecting, a query
 * may be either a base table or a view that joins data between related tables.
 * <p>
 * To view the schemas and queries exposed in a given folder, add a Query web part
 * to your portal page and choose the option "Show the list of tables in this schema"
 * in the part configuration page. Alternatively, if it is exposed, click on the Query
 * tab across the top of the main part of the page.
 * <p>Example:
 * <p>
 * <code>
 * <pre>
 *     Connection cn = new Connection("https://labkey.org");
 *     SelectRowsCommand cmd = new SelectRowsCommand("study", "Physical Exam");
 *     SelectRowsResponse response = cmd.execute(cn, "Home/Study/demo");
 *     for(Map&lt;String,Object&gt; row : response.getRows())
 *     {
 *         System.out.println(row.get("ParticipantId") + " weighs " + row.get("APXwtkg"));
 *     }
 * </pre>
 * </code>
 * <p>
 */
public class SelectRowsCommand extends Command
{
    private String _schemaName;
    private String _queryName;
    private String _viewName;
    private List<String> _columns = new ArrayList<String>();
    private int _maxRows = -1;
    private int _offset = 0;
    private List<Sort> _sorts = new ArrayList<Sort>();
    private List<Filter> _filters = new ArrayList<Filter>();

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
     * @param columns The explicit column list, or null to request the default set of columns.
     */
    public void setColumns(List<String> columns)
    {
        _columns = columns;
    }

    /**
     * Returns the current row limit value. Defaults to -1, meaning return all rows.
     * @return The current row limit value.
     */
    public int getMaxRows()
    {
        return _maxRows;
    }

    /**
     * Sets the current row limit value. If this is set to a positive value, only
     * the first <code>maxRows</code> rows will be returned from the server.
     * @param maxRows The maximum number of rows to return, or -1 to get all rows (default)
     */
    public void setMaxRows(int maxRows)
    {
        _maxRows = maxRows;
    }

    /**
     * Returns the index of the first row in the resultset to return (defaults to 0).
     * @return The current offset index.
     */
    public int getOffset()
    {
        return _offset;
    }

    /**
     * Sets the index of the first row in the resultset to return from the server.
     * Use this in conjunction with {@link #setMaxRows(int)} to return pages of
     * rows at a time from the server.
     * @param offset The current offset index.
     */
    public void setOffset(int offset)
    {
        _offset = offset;
    }

    /**
     * Returns the current list of sort definitions.
     * @return The current list of sort definitions, or null if none are defined.
     */
    public List<Sort> getSorts()
    {
        return _sorts;
    }

    /**
     * Sets the current set of sort definitions.
     * @param sorts The new list of sort definitions.
     */
    public void setSorts(List<Sort> sorts)
    {
        _sorts = sorts;
    }

    /**
     * Adds a new sort definition to the current list.
     * @param sort The new sort definition.
     */
    public void addSort(Sort sort)
    {
        if(_sorts == null)
            _sorts = new ArrayList<Sort>();
        _sorts.add(sort);
    }

    /**
     * Constructs and adds a new sort definition to the current list.
     * This is equivallent to calling <code>addSort(new Sort(columnName, direction))</code>
     * @param columnName The column name.
     * @param direction The sort direction.
     * @see Sort
     */
    public void addSort(String columnName, Sort.Direction direction)
    {
        addSort(new Sort(columnName, direction));
    }

    /**
     * Returns the current list of filters, or null if none are defined.
     * @return The current list of filters.
     */
    public List<Filter> getFilters()
    {
        return _filters;
    }

    /**
     * Sets the current list of filters.
     * @param filters The new list of filters.
     */
    public void setFilters(List<Filter> filters)
    {
        _filters = filters;
    }

    /**
     * Adds a new filter to the list.
     * @param filter The new filter definition.
     */
    public void addFilter(Filter filter)
    {
        getFilters().add(filter);
    }

    /**
     * Constructs and adds a new filter to the list. This is equivallent to
     * <code>addFilter(new Filter(columnName, value, operator))</code>
     * @param columnName The column name.
     * @param value The filter value. 
     * @param operator The filter operator.
     * @see Filter
     */
    public void addFilter(String columnName, Object value, Filter.Operator operator)
    {
        addFilter(new Filter(columnName, value, operator));
    }

    public SelectRowsResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (SelectRowsResponse)(super.execute(connection, folderPath));
    }

    /**
     * Overridden to create a SelectRowsResponse object.
     * @param text The response text
     * @param status The HTTP status code
     * @param contentType The Content-Type header value.
     * @param json The parsed JSONObject (or null if JSON was not returned).
     * @return A SelectRowsResponse object.
     */
    protected CommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SelectRowsResponse(text, status, contentType, json);
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("schemaName", getSchemaName());
        params.put("query.queryName", getQueryName());
        if(null != getViewName())
            params.put("query.viewName", getViewName());
        if(null != getColumns() && getColumns().size() > 0)
        {
            StringBuilder collist = new StringBuilder();
            String sep = "";
            for(String col : getColumns())
            {
                collist.append(sep);
                collist.append(col);
                sep = ",";
            }
            params.put("query.columns", collist);
        }
        if(getMaxRows() > 0)
            params.put("query.maxRows", getMaxRows());
        else
            params.put("query.showRows", "all");
        if(getOffset() > 0)
            params.put("query.offset", getOffset());
        if(null != getSorts() && getSorts().size() > 0)
            params.put("query.sort", getSortQueryStringParam());

        if(null != getFilters())
        {
            for(Filter filter : getFilters())
                params.put("query." + filter.getQueryStringParamName(), filter.getQueryStringParamValue());
        }

        return params;
    }

    /**
     * Constructs the sort query string parameter from the current list of
     * sort definitions. The sort query string parameter is in the form of
     * <i>[-]column,[-]column,...</i> where the optional - is used for
     * a descending sort direction.
     * @return The sort query string parameter.
     */
    protected String getSortQueryStringParam()
    {
        StringBuilder param = new StringBuilder();
        String sep = "";
        for(Sort sort : getSorts())
        {
            param.append(sep);
            if(sort.getDirection() == Sort.Direction.DESCENDING)
                param.append("-");
            param.append(sort.getColumnName());
            sep = ",";
        }
        return param.toString();
    }
}