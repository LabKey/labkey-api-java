/*
 * Copyright (c) 2009-2014 LabKey Corporation
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
package org.labkey.remoteapi.assay.nab.model;

import java.util.List;
import java.util.Map;

public class NAbRun
{
    private final long _runId;
    private final Map<String, Object> _properties;
    private final String _containerPath;
    private final String _containerId;
    private final long[] _cutoffs;
    private final NAbSample[] _samples;
    private NAbWellGroup _cellControl;
    private NAbWellGroup _virusControl;

    public NAbRun(Map<String, Object> properties)
    {
        _runId = ((Number) properties.get("runId")).longValue();
        _properties = (Map<String, Object>) properties.get("properties");
        _containerPath = (String) properties.get("containerPath");
        _containerId = (String) properties.get("containerId");
        List<Object> cutoffs = (List<Object>) properties.get("cutoffs");
        _cutoffs = new long[cutoffs.size()];
        for (int i = 0; i < cutoffs.size(); i++)
            _cutoffs[i] = ((Number) cutoffs.get(i)).longValue();

        List<Map<String, Object>> samples = (List<Map<String, Object>>) properties.get("samples");
        _samples = new NAbSample[samples.size()];
        for (int i = 0; i < samples.size(); i++)
            _samples[i] = new NAbSample(samples.get(i), _cutoffs);

        // TODO: These don't seem to work right anymore - ClassCastException happens every time
//        _cellControl = new NAbWellGroup((Map<String, Object>) properties.get("cellControls"));
//        _virusControl = new NAbWellGroup((Map<String, Object>) properties.get("virusControls"));
    }

    public long getRunId()
    {
        return _runId;
    }

    public Map<String, Object> getProperties()
    {
        return _properties;
    }

    public String getContainerPath()
    {
        return _containerPath;
    }

    public String getContainerId()
    {
        return _containerId;
    }

    public long[] getCutoffs()
    {
        return _cutoffs;
    }

    public NAbSample[] getSamples()
    {
        return _samples;
    }

    public NAbWellGroup getCellControl()
    {
        return _cellControl;
    }

    public NAbWellGroup getVirusControl()
    {
        return _virusControl;
    }
}
