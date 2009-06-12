/*
 * Copyright (c) 2009 LabKey Corporation
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
package org.labkey.remoteapi.assay.nab;

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.query.ContainerFilter;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/*
 * User: brittp
 * Date: May 15, 2009
 * Time: 11:22:43 AM
 */

/**
 * Command for obtaining information about the current assay definitions
 * in a particular folder.
 * <p>
 * By default, this command returns information about all assays, but
 * you may use the various setters to filter this list to assays of a given
 * name, type or id.
 */
public class NAbRunsCommand extends Command
{
    private String _assayName;
    private boolean _includeStats = true;
    private boolean _includeWells = true;
    private boolean _includeFitParameters = true;
    private boolean _calculateNeut = true;
    private Integer _offset;
    private Integer _maxRows;
    private String _sort;
    private String _sortDirection;
    private ContainerFilter _containerFilter;

    /**
     * Constructs a new AssayListCommand object.
     */
    public NAbRunsCommand()
    {
        super("nabassay", "getNAbRuns");
    }

    /**
     * Constructs a new NAbRunsCommand which is a copy of the source command
     * @param source The source NAbRunsCommand
     */
    public NAbRunsCommand(NAbRunsCommand source)
    {
        super(source);
        _assayName = source._assayName;
        _includeStats = source._includeStats;
        _includeWells = source._includeWells;
        _includeFitParameters = source._includeFitParameters;
        _calculateNeut = source._calculateNeut;
        _offset = source._offset;
        _maxRows = source._maxRows;
        _sort = source._sort;
        _sortDirection = source._sortDirection;
        _containerFilter = source._containerFilter;
    }

    public NAbRunsResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (NAbRunsResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new NAbRunsResponse(text, status, contentType, json, this.copy());
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();

        if (null != getAssayName())
            params.put("assayName", getAssayName());
        params.put("includeStats", isIncludeStats());
        params.put("includeWells", isIncludeWells());
        params.put("includeFitParameters", isIncludeFitParameters());
        params.put("calculateNeut", isCalculateNeut());
        if (null != getOffset())
            params.put("start", getOffset());
        if (null != getMaxRows())
            params.put("limit", getMaxRows());
        if (null != getSort())
            params.put("sort", getSort());
        if (null != getSortDirection())
            params.put("dir", getSortDirection());
        if(getContainerFilter() != null)
            params.put("containerFilter", getContainerFilter().name());
        return params;
    }

    @Override
    public NAbRunsCommand copy()
    {
        return new NAbRunsCommand(this);
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

    public String getAssayName()
    {
        return _assayName;
    }

    public void setAssayName(String assayName)
    {
        _assayName = assayName;
    }

    public boolean isIncludeStats()
    {
        return _includeStats;
    }

    public void setIncludeStats(boolean includeStats)
    {
        _includeStats = includeStats;
    }

    public boolean isIncludeWells()
    {
        return _includeWells;
    }

    public void setIncludeWells(boolean includeWells)
    {
        _includeWells = includeWells;
    }

    public boolean isIncludeFitParameters()
    {
        return _includeFitParameters;
    }

    public void setIncludeFitParameters(boolean includeFitParameters)
    {
        _includeFitParameters = includeFitParameters;
    }

    public boolean isCalculateNeut()
    {
        return _calculateNeut;
    }

    public void setCalculateNeut(boolean calculateNeut)
    {
        _calculateNeut = calculateNeut;
    }

    public Integer getOffset()
    {
        return _offset;
    }

    public void setOffset(Integer offset)
    {
        _offset = offset;
    }

    public Integer getMaxRows()
    {
        return _maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
        _maxRows = maxRows;
    }

    public String getSort()
    {
        return _sort;
    }

    public void setSort(String sort)
    {
        _sort = sort;
    }

    public String getSortDirection()
    {
        return _sortDirection;
    }

    public void setSortDirection(String sortDirection)
    {
        _sortDirection = sortDirection;
    }
}