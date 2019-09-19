package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.ResponseObject;

public class PropertyDescriptor extends ResponseObject
{
    private String _name;
    private String _label;
    private String _description;
    private Boolean _hidden = false;
    private Boolean _required = false;
    private String _PHI;
    private Long _propertyId;
    private String _format;
    private Boolean _measure = false;
    private Boolean _dimension = false;
    private String _propertyURI;
    private String _rangeURI;
    private String _lookupSchema;
    private String _lookupQuery;
    private String _lookupContainer;

    public PropertyDescriptor()
    {
        super(null);
    }

    public PropertyDescriptor(String name, String rangeURI)
    {
        super(null);
        _name = name;
        _rangeURI = rangeURI;
    }

    public PropertyDescriptor(String name, String label, String rangeURI)
    {
        super(null);
        _name = name;
        _label = label;
        _rangeURI = rangeURI;
    }

    public PropertyDescriptor(JSONObject json)
    {
        super(json);

        _name = (String)json.get("name");
        _label = (String)json.get("label");
        _description = (String)json.get("description");

        if (json.get("hidden") != null)
            _hidden = (Boolean)json.get("hidden");
        if (json.get("required") != null)
            _required = (Boolean)json.get("required");

        _PHI = (String)json.get("PHI");
        _propertyId = (Long)json.get("propertyId");
        _format = (String)json.get("format");

        if (json.get("measure") != null)
            _measure = (Boolean)json.get("measure");
        if (json.get("dimension") != null)
            _dimension = (Boolean)json.get("dimension");

        _propertyURI = (String)json.get("propertyURI");
        _rangeURI = (String)json.get("rangeURI");

        if (json.get("lookupSchema") != null)
            _lookupSchema = (String)json.get("lookupSchema");
        if (json.get("lookupQuery") != null)
            _lookupQuery = (String)json.get("lookupQuery");
        if (json.get("lookupContainer") != null)
            _lookupContainer = (String)json.get("lookupContainer");
    }

    public JSONObject toJSONObject()
    {
        JSONObject result = new JSONObject();

        if (getAllProperties() != null)
            result.putAll(getAllProperties());

        result.put("name", _name);
        result.put("label", _label);
        result.put("description", _description);
        result.put("hidden", _hidden);
        result.put("required", _required);
        result.put("PHI", _PHI);
        result.put("propertyId", _propertyId);
        result.put("format", _format);
        result.put("measure", _measure);
        result.put("dimension", _dimension);
        result.put("propertyURI", _propertyURI);
        result.put("rangeURI", _rangeURI);

        if (_lookupQuery != null && _lookupSchema != null)
        {
            result.put("lookupSchema", _lookupSchema);
            result.put("lookupQuery", _lookupQuery);
            result.put("lookupContainer", _lookupContainer);
        }
        return result;
    }

    /**
     * Convenience function for creating lookups
     * @param schema
     * @param query
     * @param container set to null for current folder
     */
    public void setLookup(String schema, String query, String container)
    {
        _lookupSchema = schema;
        _lookupQuery = query;
        _lookupContainer = container;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getLabel()
    {
        return _label;
    }

    public void setLabel(String label)
    {
        _label = label;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String description)
    {
        _description = description;
    }

    public Boolean getHidden()
    {
        return _hidden;
    }

    public void setHidden(Boolean hidden)
    {
        _hidden = hidden;
    }

    public Boolean getRequired()
    {
        return _required;
    }

    public void setRequired(Boolean required)
    {
        _required = required;
    }

    public String getPHI()
    {
        return _PHI;
    }

    public void setPHI(String PHI)
    {
        _PHI = PHI;
    }

    public Long getPropertyId()
    {
        return _propertyId;
    }

    public void setPropertyId(Long propertyId)
    {
        _propertyId = propertyId;
    }

    public String getFormat()
    {
        return _format;
    }

    public void setFormat(String format)
    {
        _format = format;
    }

    public Boolean getMeasure()
    {
        return _measure;
    }

    public void setMeasure(Boolean measure)
    {
        _measure = measure;
    }

    public Boolean getDimension()
    {
        return _dimension;
    }

    public void setDimension(Boolean dimension)
    {
        _dimension = dimension;
    }

    public String getPropertyURI()
    {
        return _propertyURI;
    }

    public void setPropertyURI(String propertyURI)
    {
        _propertyURI = propertyURI;
    }

    public String getRangeURI()
    {
        return _rangeURI;
    }

    public void setRangeURI(String rangeURI)
    {
        _rangeURI = rangeURI;
    }
}
