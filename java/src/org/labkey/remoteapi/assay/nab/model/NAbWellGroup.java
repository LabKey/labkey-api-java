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
package org.labkey.remoteapi.assay.nab.model;

import java.util.List;
import java.util.Map;

/*
 * User: brittp
 * Date: May 15, 2009
 * Time: 1:20:49 PM
 */
public class NAbWellGroup
{
    private Long _min;
    private Long _max;
    private Double _mean;
    private Double _stddev;
    private NAbWell[] _wells;

    public NAbWellGroup(Map<String, Object> properties)
    {
        if (properties.containsKey("min"))
        {
            _min = ((Number) properties.get("min")).longValue();
            _max = ((Number) properties.get("max")).longValue();
            _mean = ((Number) properties.get("mean")).doubleValue();
            _stddev = ((Number) properties.get("stddev")).doubleValue();
        }

        if (properties.keySet().contains("wells"))
        {
            List<Map<String, Object>> wells = (List<Map<String, Object>>) properties.get("wells");
            _wells = new NAbWell[wells.size()];
            for (int i = 0; i < wells.size(); i++)
                _wells[i] = new NAbWell(wells.get(i));
        }
    }

    public Long getMin()
    {
        return _min;
    }

    public Long getMax()
    {
        return _max;
    }

    public Double getMean()
    {
        return _mean;
    }

    public Double getStddev()
    {
        return _stddev;
    }

    public NAbWell[] getWells()
    {
        return _wells;
    }
}