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
 * Time: 1:21:41 PM
 */

public class NAbWell
{
    private long _row;
    private long _column;
    private long _value;

    public NAbWell(Map<String, Object> properties)
    {
        _row = ((Number) properties.get("row")).longValue();
        _column = ((Number) properties.get("column")).longValue();
        _value = ((Number) properties.get("value")).longValue();
    }

    public long getRow()
    {
        return _row;
    }

    public long getColumn()
    {
        return _column;
    }

    public long getValue()
    {
        return _value;
    }
}