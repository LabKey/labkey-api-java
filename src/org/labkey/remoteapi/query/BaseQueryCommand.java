/*
 * Copyright (c) 2009-2018 LabKey Corporation
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
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseQueryCommand<ResponseType extends CommandResponse> extends PostCommand<ResponseType>
{
    protected int _maxRows = -1;
    protected int _offset = 0;
    protected List<Sort> _sorts;
    protected List<Filter> _filters;
    protected ContainerFilter _containerFilter;
    private Map<String, String> _queryParameters = new HashMap<>();
    private boolean _ignoreFilter = false;
    private boolean _includeMetadata = true;
    private boolean _includeTotalCount = true;

    public BaseQueryCommand(BaseQueryCommand<ResponseType> source)
    {
        super(source);
        _containerFilter = source._containerFilter;
        _offset = source._offset;
        _maxRows = source._maxRows;

        //deep copy sorts and filters lists
        for (Sort sort : source._sorts)
        {
            _sorts.add(new Sort(sort));
        }

        for (Filter filter : source._filters)
        {
            _filters.add(new Filter(filter));
        }

        _queryParameters.putAll(source._queryParameters);
    }

    public BaseQueryCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    /**
     * Returns the current row limit value. Defaults to -1, meaning return all rows.
     *
     * @return The current row limit value.
     */
    public int getMaxRows()
    {
        return _maxRows;
    }

    /**
     * Sets the current row limit value. If this is set to a positive value, only
     * the first <code>maxRows</code> rows will be returned from the server.
     *
     * @param maxRows The maximum number of rows to return, or -1 to get all rows (default)
     */
    public void setMaxRows(int maxRows)
    {
        _maxRows = maxRows;
    }

    /**
     * Returns the index of the first row in the resultset to return (defaults to 0).
     *
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
     *
     * @param offset The current offset index.
     */
    public void setOffset(int offset)
    {
        _offset = offset;
    }

    /**
     * Returns the current list of sort definitions.
     *
     * @return The current list of sort definitions, or null if none are defined.
     */
    public List<Sort> getSorts()
    {
        return _sorts;
    }

    /**
     * Sets the current set of sort definitions.
     *
     * @param sorts The new list of sort definitions.
     */
    public void setSorts(List<Sort> sorts)
    {
        _sorts = sorts;
    }

    /**
     * Adds a new sort definition to the current list.
     *
     * @param sort The new sort definition.
     */
    public void addSort(Sort sort)
    {
        if (_sorts == null)
            _sorts = new ArrayList<>();
        _sorts.add(sort);
    }

    /**
     * Constructs and adds a new sort definition to the current list.
     * This is equivalent to calling <code>addSort(new Sort(columnName, direction))</code>
     *
     * @param columnName The column name.
     * @param direction  The sort direction.
     * @see org.labkey.remoteapi.query.Sort
     */
    public void addSort(String columnName, Sort.Direction direction)
    {
        addSort(new Sort(columnName, direction));
    }

    /**
     * Returns the current list of filters, or null if none are defined.
     *
     * @return The current list of filters.
     */
    public List<Filter> getFilters()
    {
        return _filters;
    }

    /**
     * Sets the current list of filters.
     *
     * @param filters The new list of filters.
     */
    public void setFilters(List<Filter> filters)
    {
        _filters = filters;
    }

    /**
     * Adds a new filter to the list.
     *
     * @param filter The new filter definition.
     */
    public void addFilter(Filter filter)
    {
        if (_filters == null)
            _filters = new ArrayList<>();
        _filters.add(filter);
    }

    /**
     * Constructs and adds a new filter to the list. This is equivalent to
     * <code>addFilter(new Filter(columnName, value, operator))</code>
     *
     * @param columnName The column name.
     * @param value      The filter value.
     * @param operator   The filter operator.
     * @see org.labkey.remoteapi.query.Filter
     */
    public void addFilter(String columnName, Object value, Filter.Operator operator)
    {
        addFilter(new Filter(columnName, value, operator));
    }

    /**
     * Returns the container filter set for this command
     *
     * @return the container filter (may be null)
     */
    public ContainerFilter getContainerFilter()
    {
        return _containerFilter;
    }

    /**
     * Sets the container filter for the sql to be executed.
     * This will cause the query to be executed over more than one container.
     *
     * @param containerFilter the filter to apply to the query (may be null)
     */
    public void setContainerFilter(ContainerFilter containerFilter)
    {
        _containerFilter = containerFilter;
    }

    public boolean isIgnoreFilter()
    {
        return _ignoreFilter;
    }

    /**
     * Pass true to ignore any filter that may be part of the chosen view. Defaults to false.
     * @param ignoreFilter Set to 'true' to ignore the view filter.
     */
    public void setIgnoreFilter(boolean ignoreFilter)
    {
        _ignoreFilter = ignoreFilter;
    }

    public boolean isIncludeMetadata()
    {
        return _includeMetadata;
    }

    /**
     * Pass false to omit metadata for the selected columns. Defaults to true.
     * @param includeMetadata Set to 'false' to omit column metadata.
     */
    public void setIncludeMetadata(boolean includeMetadata)
    {
        _includeMetadata = includeMetadata;
    }

    public boolean isIncludeTotalCount()
    {
        return _includeTotalCount;
    }

    /**
     * Pass false to omit total count from the response. Default is true.
     * @param includeTotalCount Set to 'false' to omit total count.
     */
    public void setIncludeTotalCount(boolean includeTotalCount)
    {
        _includeTotalCount = includeTotalCount;
    }

    /**
     @return Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     */
    public Map<String, String> getQueryParameters()
    {
        return _queryParameters;
    }

    /**
     Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     @param parameters the set of parameters
     */
    public void setQueryParameters(Map<String, String> parameters)
    {
        _queryParameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = super.getParameters();

        if (getOffset() > 0)
            params.put("query.offset", getOffset());

        if (getMaxRows() >= 0)
            params.put("query.maxRows", getMaxRows());
        else
            params.put("query.showRows", "all");

        if (null != getSorts() && getSorts().size() > 0)
            params.put("query.sort", Sort.getSortQueryStringParam(getSorts()));

        if (null != getFilters())
        {
            for(Filter filter : getFilters())
                params.put("query." + filter.getQueryStringParamName(), filter.getQueryStringParamValue());
        }

        if (getContainerFilter() != null)
            params.put("containerFilter", getContainerFilter().name());

        for (Map.Entry<String, String> entry : getQueryParameters().entrySet())
        {
            params.put("query.param." + entry.getKey(), entry.getValue());
        }

        if (!isIncludeTotalCount())
            params.put("includeTotalCount", isIncludeTotalCount());

        if (!isIncludeMetadata())
            params.put("includeMetadata", isIncludeMetadata());

        if (isIgnoreFilter())
            params.put("query.ignoreFilter", isIgnoreFilter());

        return params;
    }
}
