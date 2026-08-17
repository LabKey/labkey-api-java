package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

import java.util.ArrayList;
import java.util.List;

public class BaseSelectRowsCommand<ResponseType extends CommandResponse> extends BaseQueryCommand<ResponseType>
{
    private List<Filter> _filters;
    private boolean _ignoreFilter = false;

    public BaseSelectRowsCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
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

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = super.getJsonObject();

        // Note: SelectRows and ExecuteSql both support offset and maxRows, but the property names are different
        if (getOffset() > 0)
            json.put("query.offset", getOffset());

        if (getMaxRows() >= 0)
            json.put("query.maxRows", getMaxRows());
        else
            json.put("query.showRows", "all");

        if (null != getFilters())
        {
            for (Filter filter : getFilters())
                json.put("query." + filter.getQueryStringParamName(), filter.getQueryStringParamValue());
        }

        if (isIgnoreFilter())
            json.put("query.ignoreFilter", isIgnoreFilter());

        return json;
    }
}
