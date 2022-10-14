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

import org.json.JSONObject;
import org.labkey.remoteapi.ResponseObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for experiment objects, including Run, Data, Batch, etc
 */
public abstract class ExpObject extends ResponseObject
{
    private Number _id;
    private String _name;
    private Map<String, Object> _properties = new HashMap<>();

    public ExpObject()
    {
        super(null);
    }

    public ExpObject(JSONObject json)
    {
        super(json.toMap());
        _id = json.optNumber("id");
        _name = json.optString("name", null);
        if (json.has("properties"))
        {
            _properties = json.optJSONObject("properties").toMap();
        }
    }

    public JSONObject toJSONObject()
    {
        JSONObject result = new JSONObject();
        result.put("id", _id);
        result.put("name", _name);
        result.put("properties", _properties);
        return result;
    }

    /** @return the auto-generated rowId for this object */
    public Integer getId()
    {
        return _id == null ? null : _id.intValue();
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
