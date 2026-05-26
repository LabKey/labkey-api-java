/*
 * Copyright (c) 2019-2026 LabKey Corporation
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
package org.labkey.remoteapi.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.ResponseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean that defines a domain for creating, updating, or reading.
 */
public class Domain extends ResponseObject
{
    private String _name;
    private String _description;
    private Long _domainId;
    private String _domainURI;
    private List<PropertyDescriptor> _fields = new ArrayList<>();

    public Domain()
    {
    }

    public Domain(String name)
    {
        _name = name;
    }

    public Domain(JSONObject json)
    {
        super(json.toMap());

        _name = json.getString("name");
        _description = json.optString("description", null);
        _domainId = json.getLong("domainId");
        _domainURI = json.getString("domainURI");

        if (json.get("fields") instanceof JSONArray)
        {
            for (Object field : json.getJSONArray("fields"))
            {
                _fields.add(new PropertyDescriptor((JSONObject)field));
            }
        }
    }

    public JSONObject toJSONObject()
    {
        return toJSONObject(false);
    }

    public JSONObject toJSONObject(boolean forProtocol)
    {
        JSONObject result = new JSONObject();

        if (getAllProperties() != null)
        {
            getAllProperties().forEach(result::put);
            // Remove response properties that should not be re-POSTed
            result.remove("success");
            result.remove("failure");
        }

        result.put("name", _name);
        result.put("description", _description);
        result.put("domainId", _domainId);
        result.put("domainURI", _domainURI);

        JSONArray fields = new JSONArray();
        result.put("fields", fields);
        for (PropertyDescriptor field : _fields)
        {
            fields.put(field.toJSONObject(forProtocol));
        }
        return result;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String description)
    {
        _description = description;
    }

    public Long getDomainId()
    {
        return _domainId;
    }

    public void setDomainId(Long domainId)
    {
        _domainId = domainId;
    }

    public String getDomainURI()
    {
        return _domainURI;
    }

    public void setDomainURI(String domainURI)
    {
        _domainURI = domainURI;
    }

    public List<PropertyDescriptor> getFields()
    {
        return _fields;
    }

    public void setFields(List<PropertyDescriptor> fields)
    {
        _fields = fields;
    }
}
