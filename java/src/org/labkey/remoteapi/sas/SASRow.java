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

import java.util.Map;
import java.util.HashMap;

/**
 * User: adam
 * Date: Feb 2, 2009
 * Time: 2:54:45 PM
 */
public class SASRow
{
    private Map<String, Object> _map = new HashMap<String, Object>();

    public void clear()
    {
        _map.clear();
    }

    // TODO: Special put for Date?

    public void add(String key, String object)
    {
        _map.put(key, object);
    }

    Map<String, Object> getMap()
    {
        return _map;
    }
}
