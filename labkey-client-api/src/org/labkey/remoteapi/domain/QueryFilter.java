package org.labkey.remoteapi.domain;

import org.labkey.remoteapi.query.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryFilter
{
    private List<Filter> _queryFilter;

    public QueryFilter(List<Filter> filters)
    {
        _queryFilter = filters;
    }

    public List<Filter> getQueryFilter()
    {
        return Collections.unmodifiableList(_queryFilter);
    }

    public void setFilters(List<Filter> filters)
    {
        _queryFilter = filters;
    }

    static public QueryFilter fromJSONString(String filterStr)
    {
        List<Filter> queryFilter = new ArrayList<>();

        String[] filterStrs = filterStr.split("&");
        for (String filter : filterStrs)
        {
            String filterExp = filter.substring(filter.indexOf("~") + 1, filter.indexOf("="));
            Filter.Operator op = Filter.Operator.getOperatorFromUrlKey(filterExp);

            String value = filter.substring(filter.lastIndexOf("=") + 1);
            queryFilter.add(new Filter((Object) value, op));
        }

        return new QueryFilter(queryFilter);
    }

    public String toJSONString()
    {
        ArrayList<String> stringForm = new ArrayList();
        for (Filter f : _queryFilter)
        {
            String filterUrlKey = f.getOperator().getUrlKey();
            String value = f.getQueryStringParamValue();
            String s = String.format("format.column~%s=%s", filterUrlKey, value);
            stringForm.add(s);
        }

        if (stringForm.size() == 1)
        {
            return stringForm.get(0);
        }
        else
        {
            return String.format("%s&%s", stringForm.get(0), stringForm.get(1));
        }
    }
}
