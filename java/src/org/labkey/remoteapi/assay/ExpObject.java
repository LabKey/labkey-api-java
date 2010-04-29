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
        result.put("properties", _properties);
        return result;
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
