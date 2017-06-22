/*
 * Copyright (c) 2010-2017 LabKey Corporation
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
package org.labkey.remoteapi.assay;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.ResponseObject;

import java.util.HashMap;
import java.util.Map;

/**
* Base class for experiment objects, including Run, Data, Batch, etc
* User: jeckels
* Date: Apr 28, 2010
*/
public abstract class ExpObject extends ResponseObject
{
    private int _id;
    private String _name;
    private Map<String, Object> _properties = new HashMap<String, Object>();

    public ExpObject()
    {
        super(null);
    }

    public ExpObject(JSONObject json)
    {
        super(json);
        _id = json.containsKey("id") ? ((Number)json.get("id")).intValue() : 0;
        _name = (String)json.get("name");
        if (json.get("properties") != null)
        {
            _properties = (Map<String, Object>)json.get("properties");
        }
    }

    public JSONObject toJSONObject()
    {
        JSONObject result = new JSONObject();
        if (_id != 0)
        {
            result.put("id", _id);
        }
        result.put("name", _name);
        result.put("properties", toJSON(_properties));
        return result;
    }

    /**
     * Temp Fix for Issue: 23708
     *      The Simple JSON library isn't properly serializing Dates, and generates invalid JSON.
     *  TODO: Investigate and replace with a different JSON library
     * @param properties Map to serialize
     * @return the JSON object corresponding to the given properties
     */
    public JSONObject toJSON(Map<String, Object> properties)
    {
        JSONObject props = new JSONObject();

        for(Map.Entry entry : properties.entrySet())
        {
            Object val = entry.getValue();
            val = val instanceof java.util.Date ?
                val.toString() :
                val instanceof java.util.Map ?
                    toJSON((java.util.Map) val):
                    val;

            props.put(entry.getKey(), val);
        }

        return props;
    }

    /** @return the auto-generated rowId for this object */
    public int getId()
    {
        return _id;
    }

    /** @param id the auto-generated rowId for this object */
    public void setId(int id)
    {
        _id = id;
    }

    /** @return the name assigned to this object */
    public String getName()
    {
        return _name;
    }

    /** @param name the name assigned to this object */
    public void setName(String name)
    {
        _name = name;
    }

    /** @return the customizable properties attached to this object (Run Properties, Batch Properties, etc) */
    public Map<String, Object> getProperties()
    {
        return _properties;
    }

    /** @param properties the customizable properties attached to this object (Run Properties, Batch Properties, etc) */
    public void setProperties(Map<String, Object> properties)
    {
        _properties = properties;
    }
}
