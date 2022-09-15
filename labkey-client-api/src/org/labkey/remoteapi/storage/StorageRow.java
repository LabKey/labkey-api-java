package org.labkey.remoteapi.storage;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StorageRow
{
    private String _type;
    private Map<String, Object> _props = new HashMap<>();

    public String getType()
    {
        return _type;
    }

    public void setType(String type)
    {
        _type = type;
    }

    public Map<String, Object> getProps()
    {
        return _props;
    }

    public void setProps(Map<String, Object> props)
    {
        _props = props;
    }

    public void setRowId(Integer rowId)
    {
        Map<String, Object> props = new HashMap<>(_props);
        props.put("rowId", rowId);
        setProps(props);
    }

    public Map<String, Object> addProp(String key, Object value)
    {
        _props.put(key, value);
        return _props;
    }

    public JSONObject toJsonObject()
    {
        JSONObject result = new JSONObject();
        result.put("type", this.getType());
        result.put("props", new JSONObject(this.getProps()));
        return result;
    }
}
