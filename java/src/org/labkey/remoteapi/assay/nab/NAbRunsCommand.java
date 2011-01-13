/*
 * Copyright (c) 2009-2011 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.labkey.remoteapi.query.BaseQueryCommand;

import java.util.Map;

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
public class NAbRunsCommand extends BaseQueryCommand<NAbRunsResponse>
{
    private String _assayName;
    private boolean _includeStats = true;
    private boolean _includeWells = true;
    private boolean _includeFitParameters = true;
    private boolean _calculateNeut = true;

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
    }

    protected NAbRunsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new NAbRunsResponse(text, status, contentType, json, this.copy());
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = super.getParameters();

        if (null != getAssayName())
            params.put("assayName", getAssayName());
        params.put("includeStats", isIncludeStats());
        params.put("includeWells", isIncludeWells());
        params.put("includeFitParameters", isIncludeFitParameters());
        params.put("calculateNeut", isCalculateNeut());
        return params;
    }

    @Override
    public NAbRunsCommand copy()
    {
        return new NAbRunsCommand(this);
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
}