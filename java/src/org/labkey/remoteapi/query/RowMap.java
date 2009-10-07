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
package org.labkey.remoteapi.query;

import java.util.Map;

/*
* User: dave
* Date: Oct 7, 2009
* Time: 11:46:02 AM
*/
public class RowMap implements Row
{
    private Map<String,Object> _row;
    private boolean _extendedFormat = false;

    public RowMap()
    {
    }

    public RowMap(Map<String, Object> row)
    {
        setMap(row);
    }

    public void setMap(Map<String,Object> row)
    {
        _row = row;
        _extendedFormat = _row.size() > 0 && row.values().iterator().next() instanceof Map;
    }

    public Object getValue(String columnName)
    {
        Object col = _row.get(columnName);
        return null == col || !_extendedFormat ? col : ((Map<String,Object>)col).get("value");
    }

    public Object getDisplayValue(String columnName)
    {
        Object col = _row.get(columnName);
        return null == col || !_extendedFormat ? null : ((Map<String,Object>)col).get("displayValue");
    }

    public String getUrl(String columnName)
    {
        Object col = _row.get(columnName);
        return null == col || !_extendedFormat ? null : (String)((Map<String,Object>)col).get("url");
    }

    public String getMvValue(String columnName)
    {
        Object col = _row.get(columnName);
        return null == col || !_extendedFormat ? null : (String)((Map<String,Object>)col).get("mvValue");
    }

    public Object getMvRawValue(String columnName)
    {
        Object col = _row.get(columnName);
        return null == col || !_extendedFormat ? null : ((Map<String,Object>)col).get("mvRawValue");
    }

    @Override
    public String toString()
    {
        return _row.toString();
    }
}
