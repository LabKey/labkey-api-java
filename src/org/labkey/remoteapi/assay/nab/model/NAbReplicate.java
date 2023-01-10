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

public class NAbReplicate extends NAbWellGroup
{
    final private double _dilution;
    final private Double _neutPercent;
    final private Double _neutPlusMinus;

    public NAbReplicate(Map<String, Object> properties)
    {
        super(properties);
        _dilution = ((Number) properties.get("dilution")).doubleValue();
        if (properties.get("neutPercent") != null)
            _neutPercent = ((Number) properties.get("neutPercent")).doubleValue();
        else
            _neutPercent = null;
        if (properties.get("neutPlusMinus") != null)
            _neutPlusMinus = ((Number) properties.get("neutPlusMinus")).doubleValue();
        else
            _neutPlusMinus = null;
    }

    public double getDilution()
    {
        return _dilution;
    }

    public Double getNeutPercent()
    {
        return _neutPercent;
    }

    public Double getNeutPlusMinus()
    {
        return _neutPlusMinus;
    }
}
