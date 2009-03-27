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
package org.labkey.remoteapi.sas;

import java.util.HashMap;
import java.util.Map;

/**
 * User: adam
 * Date: Feb 2, 2009
 * Time: 2:54:45 PM
 */
public class SASRow
{
    private Map<String, Object> _map = new HashMap<String, Object>();

    public void put(String key, String value)
    {
        _map.put(key, value);
    }

    public void put(String key, double value)
    {
        // Missing values are passed as NaN
        if (Double.isNaN(value))
        {
            _map.put(key, null);
        }
        else
        {
            // Temp hack to handle fact that SAS only sets doubles, but server-side bean converters will throw for integer fields
            // TODO: Make server more lenient in this case (accept double format when expecting integer)
            Double delta = Math.abs(value - Math.round(value));

            if (delta < 0.000000001)
                _map.put(key, new Double(value).longValue());
            else
                _map.put(key, value);
        }
    }

    Map<String, Object> getMap()
    {
        return _map;
    }
}
