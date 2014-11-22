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

import java.util.Map;
import java.util.List;/*
 * User: brittp
 * Date: May 15, 2009
 * Time: 1:19:40 PM
 */

public class NAbSample
{
    private Map<String, Object> _properties;
    private String _wellgroupName;
    private Long _minDilution;
    private Long _maxDilution;
    private long _objectId;
    private Double _fitError;
    private NAbCurveParameters _fitParameters;
    private NAbReplicate[] _replicates;
    private NAbNeutralizationResult[] _neutralizationResults;

    public NAbSample(Map<String, Object> properties, long[] cutoffs)
    {
        _properties = (Map<String, Object>) properties.get("properties");
        _wellgroupName = (String) properties.get("wellgroupName");
        _objectId = ((Number) properties.get("objectId")).longValue();
        if (properties.containsKey("minDilution"))
        {
            _minDilution = ((Number) properties.get("minDilution")).longValue();
            _maxDilution = ((Number) properties.get("maxDilution")).longValue();
        }

        if (properties.containsKey("fitError"))
            _fitError = ((Number) properties.get("fitError")).doubleValue();

        if (properties.containsKey("fitParameters"))
            _fitParameters = new NAbCurveParameters((Map<String, Object>) properties.get("fitParameters"));

        if (properties.containsKey("replicates"))
        {
            List<Map<String, Object>> replicates = (List<Map<String, Object>>) properties.get("replicates");
            _replicates = new NAbReplicate[replicates.size()];
            for (int i = 0; i < replicates.size(); i++)
                _replicates[i] = new NAbReplicate(replicates.get(i));
        }

        if (cutoffs != null && cutoffs.length > 0)
        {
            // check to see if we have neutralization with this query:
            if (properties.containsKey("curveIC" + cutoffs[0]))
            {
                _neutralizationResults = new NAbNeutralizationResult[cutoffs.length];
                for (int i = 0; i < cutoffs.length; i++)
                {
                    long cutoff = cutoffs[i];
                    double curveBasedDilution = convert(properties.get("curveIC" + cutoff));
                    double pointBasedDilution = convert(properties.get("pointIC" + cutoff));
                    _neutralizationResults[i] = new NAbNeutralizationResult(cutoff, curveBasedDilution, pointBasedDilution);
                }
            }
        }
    }

    private double convert(Object ic)
    {
       return ic instanceof Number ? ((Number) ic).doubleValue() : Double.valueOf((String) ic);
    }

    public Map<String, Object> getProperties()
    {
        return _properties;
    }

    public String getWellgroupName()
    {
        return _wellgroupName;
    }

    public Long getMinDilution()
    {
        return _minDilution;
    }

    public Long getMaxDilution()
    {
        return _maxDilution;
    }

    public Double getFitError()
    {
        return _fitError;
    }

    public NAbCurveParameters getFitParameters()
    {
        return _fitParameters;
    }

    public NAbReplicate[] getReplicates()
    {
        return _replicates;
    }

    public NAbNeutralizationResult[] getNeutralizationResults()
    {
        return _neutralizationResults;
    }

    public long getObjectId()
    {
        return _objectId;
    }
}
