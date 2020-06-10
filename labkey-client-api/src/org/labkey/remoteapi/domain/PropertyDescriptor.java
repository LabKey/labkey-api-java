package org.labkey.remoteapi.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.ResponseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private Boolean _mvEnabled = false;
    private String _propertyURI;
    private String _rangeURI;
    private String _lookupSchema;
    private String _lookupQuery;
    private String _lookupContainer;
    private List<ConditionalFormat> _conditionalFormats = new ArrayList<>();

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

        JSONArray cfs = (JSONArray) json.get("conditionalFormats");
        cfs.forEach(cf -> {
            JSONObject cfObj = (JSONObject) cf;
            _conditionalFormats.add(
                    new ConditionalFormat(
                    (String) cfObj.get("filter"),
                    (String) cfObj.get("textColor"),
                    (String) cfObj.get("backgroundColor"),
                    (Boolean) cfObj.get("bold"),
                    (Boolean) cfObj.get("italic"),
                    (Boolean) cfObj.get("strikethrough"))
            );
        });

        if (json.get("lookupSchema") != null)
            _lookupSchema = (String)json.get("lookupSchema");
        if (json.get("lookupQuery") != null)
            _lookupQuery = (String)json.get("lookupQuery");
        if (json.get("lookupContainer") != null)
            _lookupContainer = (String)json.get("lookupContainer");
        if (json.get("mvEnabled") != null)
            _mvEnabled = (Boolean)json.get("mvEnabled");
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
        result.put("label", _label);
        result.put("description", _description);
        result.put("hidden", _hidden);
        result.put("required", _required);
        result.put("PHI", _PHI);
        result.put("propertyId", _propertyId);
        result.put("format", _format);
        result.put("measure", _measure);
        result.put("mvEnabled", _mvEnabled);
        result.put("dimension", _dimension);
        result.put("propertyURI", _propertyURI);
        result.put("conditionalFormats", serializeConditionalFormats());

        if (_rangeURI != null)
            result.put("rangeURI", _rangeURI);

        if (_lookupQuery != null && _lookupSchema != null)
        {
            result.put("lookupSchema", _lookupSchema);
            result.put("lookupQuery", _lookupQuery);
            result.put("lookupContainer", _lookupContainer);
        }

        if (forProtocol)
        {
            result.put("url", result.get("URL"));
            result.remove("URL");
            result.put("phi", result.get("PHI"));
            result.remove("PHI");
        }

        return result;
    }

    private JSONArray serializeConditionalFormats()
    {
        JSONArray cfs = new JSONArray();
        for (ConditionalFormat conditionalFormat : _conditionalFormats)
        {
            JSONObject cf = conditionalFormat.toJSON();
            cfs.add(cf);
        }
        return cfs;
    }

    /**
     * Convenience function for creating lookups
     * @param schema the schema for the lookup
     * @param query the query for the lookup
     * @param container set to null for current folder
     * @return property descriptor for the lookup in the given container for the given schema and query
     */
    public PropertyDescriptor setLookup(String schema, String query, String container)
    {
        _lookupSchema = schema;
        _lookupQuery = query;
        _lookupContainer = container;
        return this;
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

    public PropertyDescriptor setLabel(String label)
    {
        _label = label;
        return this;
    }

    public String getDescription()
    {
        return _description;
    }

    public PropertyDescriptor setDescription(String description)
    {
        _description = description;
        return this;
    }

    public Boolean getHidden()
    {
        return _hidden;
    }

    public PropertyDescriptor setHidden(Boolean hidden)
    {
        _hidden = hidden;
        return this;
    }

    public Boolean getRequired()
    {
        return _required;
    }

    public PropertyDescriptor setRequired(Boolean required)
    {
        _required = required;
        return this;
    }

    public String getPHI()
    {
        return _PHI;
    }

    public PropertyDescriptor setPHI(String PHI)
    {
        _PHI = PHI;
        return this;
    }

    public Long getPropertyId()
    {
        return _propertyId;
    }

    public PropertyDescriptor setPropertyId(Long propertyId)
    {
        _propertyId = propertyId;
        return this;
    }

    public String getFormat()
    {
        return _format;
    }

    public PropertyDescriptor setFormat(String format)
    {
        _format = format;
        return this;
    }

    public Boolean getMeasure()
    {
        return _measure;
    }

    public PropertyDescriptor setMeasure(Boolean measure)
    {
        _measure = measure;
        return this;
    }

    public Boolean getDimension()
    {
        return _dimension;
    }

    public PropertyDescriptor setDimension(Boolean dimension)
    {
        _dimension = dimension;
        return this;
    }

    public String getPropertyURI()
    {
        return _propertyURI;
    }

    public PropertyDescriptor setPropertyURI(String propertyURI)
    {
        _propertyURI = propertyURI;
        return this;
    }

    public String getRangeURI()
    {
        return _rangeURI;
    }

    public PropertyDescriptor setRangeURI(String rangeURI)
    {
        _rangeURI = rangeURI;
        return this;
    }

    public Boolean getMvEnabled()
    {
        return _mvEnabled;
    }

    public PropertyDescriptor setMvEnabled(Boolean mvEnabled)
    {
        _mvEnabled = mvEnabled;
        return this;
    }

    public PropertyDescriptor setConditionalFormats(List<ConditionalFormat> conditionalFormats)
    {
        _conditionalFormats = conditionalFormats;
        return this;
    }

    public List<ConditionalFormat> getConditionalFormats()
    {
        return Collections.unmodifiableList(_conditionalFormats);
    }
}
