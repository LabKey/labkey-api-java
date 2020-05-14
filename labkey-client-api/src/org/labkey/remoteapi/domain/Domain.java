package org.labkey.remoteapi.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.ResponseObject;

import java.util.ArrayList;
import java.util.List;

public class Domain extends ResponseObject
{
    private String _name;
    private String _description;
    private Long _domainId;
    private String _domainURI;
    private List<PropertyDescriptor> _fields = new ArrayList<>();

    public Domain()
    {
        super(null);
    }

    public Domain(String name)
    {
        super(null);
        _name = name;
    }

    public Domain(JSONObject json)
    {
        super(json);

        _name = (String)json.get("name");
        _description = (String)json.get("description");
        _domainId = (Long)json.get("domainId");
        _domainURI = (String)json.get("domainURI");

        if (json.get("fields") instanceof JSONArray)
        {
            for (Object field : ((JSONArray)json.get("fields")))
            {
                _fields.add(new PropertyDescriptor((JSONObject)field));
            }
        }

        _allProperties.remove("success");
        _allProperties.remove("failure");
    }

    public JSONObject toJSONObject()
    {
        return toJSONObject(false);
    }

    public JSONObject toJSONObject(boolean forProtocol)
    {
        JSONObject result = new JSONObject();

        if (getAllProperties() != null)
            result.putAll(getAllProperties());

        result.put("name", _name);
        result.put("description", _description);
        result.put("domainId", _domainId);
        result.put("domainURI", _domainURI);

        JSONArray fields = new JSONArray();
        result.put("fields", fields);
        for (PropertyDescriptor field : _fields)
        {
            fields.add(field.toJSONObject(forProtocol));
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
