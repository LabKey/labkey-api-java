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
package org.labkey.remoteapi.assay.nab.model;

import java.util.Map;

public class NAbCurveParameters
{
    private final double _asymmetry;
    private final double _inflection;
    private final double _slope;
    private final double _max;
    private final double _min;

    public NAbCurveParameters(Map<String, Object> properties)
    {
        _asymmetry = ((Number) properties.get("asymmetry")).doubleValue();
        _inflection = ((Number) properties.get("inflection")).doubleValue();
        _slope = ((Number) properties.get("slope")).doubleValue();
        _max = ((Number) properties.get("max")).doubleValue();
        _min = ((Number) properties.get("min")).doubleValue();
    }

    public double getAsymmetry()
    {
        return _asymmetry;
    }

    public double getInflection()
    {
        return _inflection;
    }

    public double getSlope()
    {
        return _slope;
    }

    public double getMax()
    {
        return _max;
    }

    public double getMin()
    {
        return _min;
    }
}
