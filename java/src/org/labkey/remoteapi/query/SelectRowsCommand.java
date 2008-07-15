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

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.CommandResponse;
import org.apache.commons.codec.EncoderException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 10:54:45 AM
*/

/**
 * This command may be used to select data from any of the LabKey schemas.
 */
public class SelectRowsCommand extends Command
{
    private String _schemaName;
    private String _queryName;
    private String _viewName;
    private String _columns;
    private int _maxRows = -1;
    private int _offset = 0;
    private List<Sort> _sorts = new ArrayList<Sort>();
    private List<Filter> _filters = new ArrayList<Filter>();

    public SelectRowsCommand(String schemaName, String queryName)
    {
        super("query", "selectRows");
        setSchemaName(schemaName);
        setQueryName(queryName);
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

    public String getViewName()
    {
        return _viewName;
    }

    public void setViewName(String viewName)
    {
        _viewName = viewName;
    }

    public String getColumns()
    {
        return _columns;
    }

    public void setColumns(String columns)
    {
        _columns = columns;
    }

    public int getMaxRows()
    {
        return _maxRows;
    }

    public void setMaxRows(int maxRows)
    {
        _maxRows = maxRows;
    }

    public int getOffset()
    {
        return _offset;
    }

    public void setOffset(int offset)
    {
        _offset = offset;
    }

    public List<Sort> getSorts()
    {
        return _sorts;
    }

    public void setSorts(List<Sort> sort)
    {
        _sorts = sort;
    }

    public void addSort(Sort sort)
    {
        if(_sorts == null)
            _sorts = new ArrayList<Sort>();
        _sorts.add(sort);
    }

    public void addSort(String columnName, Sort.Direction direction)
    {
        addSort(new Sort(columnName, direction));
    }

    public List<Filter> getFilters()
    {
        return _filters;
    }

    public void setFilters(List<Filter> filters)
    {
        _filters = filters;
    }

    public void addFilter(Filter filter)
    {
        getFilters().add(filter);
    }

    public void addFilter(String columnName, Object value, Filter.Operator operator)
    {
        addFilter(new Filter(columnName, value, operator));
    }

    public SelectRowsResponse execute(Connection connection, String folderPath) throws IOException, EncoderException
    {
        return (SelectRowsResponse)(super.execute(connection, folderPath));
    }

    protected CommandResponse createResponse(String text, int status)
    {
        return new SelectRowsResponse(text, status);
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("schemaName", getSchemaName());
        params.put("query.queryName", getQueryName());
        if(null != getViewName())
            params.put("query.viewName", getViewName());
        if(null != getColumns())
            params.put("query.columns", getColumns());
        if(getMaxRows() > 0)
            params.put("query.maxRows", getMaxRows());
        else
            params.put("query.showAllRows", true);
        if(getOffset() > 0)
            params.put("query.offset", getOffset());
        if(getSorts().size() > 0)
            params.put("query.sort", getSortQueryStringParam());

        for(Filter filter : getFilters())
            params.put("query." + filter.getQueryStringParamName(), filter.getQueryStringParamValue());

        return params;
    }

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