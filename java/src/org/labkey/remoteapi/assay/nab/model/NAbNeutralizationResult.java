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

/*
 * User: brittp
 * Date: May 15, 2009
 * Time: 1:27:21 PM
 */

public class NAbNeutralizationResult
{
    private long _cutoff;
    private double _curveBasedDilution;
    private double _pointBasedDilution;

    public NAbNeutralizationResult(long cutoff, double curveBasedDilution, double pointBasedDilution)
    {

        _cutoff = cutoff;
        _curveBasedDilution = curveBasedDilution;
        _pointBasedDilution = pointBasedDilution;
    }

    public long getCutoff()
    {
        return _cutoff;
    }

    public double getCurveBasedDilution()
    {
        return _curveBasedDilution;
    }

    public double getPointBasedDilution()
    {
        return _pointBasedDilution;
    }
}