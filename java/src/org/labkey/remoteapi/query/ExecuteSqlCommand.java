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
import org.labkey.remoteapi.PostCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 22, 2008
* Time: 1:32:57 PM
*/

/**
 * Command for executing arbitrary LabKey SQL.
 * <p>
 * LabKey SQL is variant of standard SQL that supports most of the SELECT-related
 * operations. For more information on LabKey SQL, see the following page:
 * <p>
 * <a href="https://www.labkey.org/wiki/home/Documentation/page.view?name=labkeySql">
 * https://www.labkey.org/wiki/home/Documentation/page.view?name=labkeySql</a>
 * <p>
 * The response of this command is exactly the same as the
 * {@link org.labkey.remoteapi.query.SelectRowsCommand}, so the response object
 * will be of type {@link org.labkey.remoteapi.query.SelectRowsResponse}.
 */
public class ExecuteSqlCommand extends PostCommand<SelectRowsResponse> implements BaseSelect
{
    private String _schemaName;
    private String _sql;
    private int _maxRows = -1;
    private int _offset = 0;
    private ContainerFilter _containerFilter;
    private boolean _includeTotalCount = true;
    private List<Sort> _sorts;
    private boolean _saveInSession = false;
    private boolean _includeDetailsColumn = false;
    private Map<String, String> _queryParameters = new HashMap<String, String>();

    /**
     * Constructs an ExecuteSqlCommand, initialized with a schema name.
     * <p>
     * When using this constructor, you must call the {@link #setSql(String)}
     * method before executing the command.
     * @param schemaName The schema name to query.
     */
    public ExecuteSqlCommand(String schemaName)
    {
        super("query", "executeSql");
        _schemaName = schemaName;
    }

    public ExecuteSqlCommand(ExecuteSqlCommand source)
    {
        super(source);
        _schemaName = source._schemaName;
        _sql = source._sql;
        _maxRows = source._maxRows;
        _offset = source._offset;
        _containerFilter = source._containerFilter;
        _includeTotalCount = source._includeTotalCount;
        _sorts = source._sorts;
        _saveInSession = source._saveInSession;
        _includeDetailsColumn = source._includeDetailsColumn;
        _queryParameters = new HashMap<String, String>(source.getQueryParameters());
    }

    /**
     * Constructs an ExecuteSqlCommand, initialized with a schema name and SQL query.
     * @param schemaName The schema name ot query.
     * @param sql The SQL query.
     */
    public ExecuteSqlCommand(String schemaName, String sql)
    {
        super("query", "executeSql");
        _schemaName = schemaName;
        _sql = sql;
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
     * Returns the current schema name.
     * @return The current schema name.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Sets the current schema name.
     * @param schemaName The new schema name to query.
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    /**
     * Returns the current SQL query.
     * @return The current SQL query.
     */
    public String getSql()
    {
        return _sql;
    }

    /**
     * Sets the SQL query to execute.
     * @param sql The new SQL query.
     */
    public void setSql(String sql)
    {
        _sql = sql;
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
     * @param maxRows The maximim number of rows to return, or -1 to get all rows (default).
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
     Include the total number of rows available (defaults to true).
     If false totalCount will equal number of rows returned (equal to maxRows unless maxRows == 0).
     @return indication of whether total count should be included or not
     */
    public boolean isIncludeTotalCount()
    {
        return _includeTotalCount;
    }

    /**
     Include the total number of rows available (defaults to true).
     If false totalCount will equal number of rows returned (equal to maxRows unless maxRows == 0).
     @param includeTotalCount setting for whether to include the total count
     */
    public void setIncludeTotalCount(boolean includeTotalCount)
    {
        _includeTotalCount = includeTotalCount;
    }

    /**
     A sort specification to apply over the rows returned by the SQL. In general, you should either include an
     ORDER BY clause in your SQL, or specific a sort specification in this config property, but not both.
     The value of this property should be a comma-delimited list of column names you want to sort by.
     Use a - prefix to sort a column in descending order
     (e.g., 'LastName,-Age' to sort first by LastName, then by Age descending).
     @return the set of sorts to apply
     */
    public List<Sort> getSorts()
    {
        return _sorts;
    }

    /**
     A sort specification to apply over the rows returned by the SQL. In general, you should either include an
     ORDER BY clause in your SQL, or specific a sort specification in this config property, but not both.
     The value of this property should be a comma-delimited list of column names you want to sort by.
     Use a - prefix to sort a column in descending order
     (e.g., 'LastName,-Age' to sort first by LastName, then by Age descending).
     @param sorts the sort specifications to apply to the query
     */
    public void setSort(List<Sort> sorts)
    {
        _sorts = sorts;
    }

    /**
     * Whether or not the definition of this query should be stored for reuse during the current session.
     * If true, all information required to recreate the query will be stored on the server and a unique query name
     * will be passed to the success callback. This temporary query name can be used by all other API methods,
     * including Query Web Part creation, for as long as the current user's session remains active.
     * @return whether to save the query definition in session or not
     */
    public boolean isSaveInSession()
    {
        return _saveInSession;
    }

    /**
     * Whether or not the definition of this query should be stored for reuse during the current session.
     * If true, all information required to recreate the query will be stored on the server and a unique query name
     * will be passed to the success callback. This temporary query name can be used by all other API methods,
     * including Query Web Part creation, for as long as the current user's session remains active.
     * @param saveInSession indication of whether to save in session or not
     */
    public void setSaveInSession(boolean saveInSession)
    {
        _saveInSession = saveInSession;
    }

    /**
     Include the Details link column in the set of columns (defaults to false).
     If included, the column will have the name "~~Details~~".
     The underlying table/query must support details links or the column will be omitted in the response.
     @return whether to include the details column in the set of columns
     */
    public boolean isIncludeDetailsColumn()
    {
        return _includeDetailsColumn;
    }

    /**
     Include the Details link column in the set of columns (defaults to false).
     If included, the column will have the name "~~Details~~".
     The underlying table/query must support details links or the column will be omitted in the response.
     @param includeDetailsColumn indication of whether to include the details column or not
     */
    public void setIncludeDetailsColumn(boolean includeDetailsColumn)
    {
        _includeDetailsColumn = includeDetailsColumn;
    }

    /**
     Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     @return the set of query parameters for the SQL references
     */
    public Map<String, String> getQueryParameters()
    {
        return _queryParameters;
    }

    /**
     Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     @param parameters a map of the named parameters to use in the underlying parameterized queries
     */
    public void setQueryParameters(Map<String, String> parameters)
    {
        _queryParameters = parameters;
    }

    /**
     * Returns the container filter set for this command
     * @return the container filter (may be null)
     */
    public ContainerFilter getContainerFilter()
    {
        return _containerFilter;
    }

    /**
     * Sets the container filter for the sql to be executed.
     * This will cause the query to be executed over more than one container.
     * @param containerFilter the filter to apply to the query (may be null)
     */
    public void setContainerFilter(ContainerFilter containerFilter)
    {
        this._containerFilter = containerFilter;
    }

    protected SelectRowsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        assert null != _schemaName : "You must set the schemaName before executing!";
        assert null != _sql : "You must set the Sql before executing!";
        return new SelectRowsResponse(text, status, contentType, json, this.copy());
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("sql", getSql());
        if(getMaxRows() >= 0)
            json.put("maxRows", getMaxRows());
        if(getOffset() > 0)
            json.put("offset", getOffset());
        if(getContainerFilter() != null)
            json.put("containerFilter", getContainerFilter().name());
        json.put("includeTotalCount", isIncludeTotalCount());
        json.put("includeDetailsColumn", isIncludeDetailsColumn());
        json.put("saveInSession", isSaveInSession());
        return json;
    }

    @Override
    public ExecuteSqlCommand copy()
    {
        return new ExecuteSqlCommand(this);
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        if(null != getSorts() && getSorts().size() > 0)
            params.put("query.sort", Sort.getSortQueryStringParam(getSorts()));
        for (Map.Entry<String, String> entry : getQueryParameters().entrySet())
        {
            params.put("query.param." + entry.getKey(), entry.getValue());
        }

        return params;
    }
}