package org.labkey.remoteapi.domain;

import org.json.JSONArray;
import org.json.JSONObject;
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
    private String _derivationDataScope;
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
        super(json.toMap());

        _name = json.getString("name");
        _label = json.optString("label", null);
        _description = json.optString("description", null);

        if (json.has("hidden"))
            _hidden = json.getBoolean("hidden");
        if (json.has("required"))
            _required = json.getBoolean("required");

        _PHI = json.optString("PHI", null);
        _propertyId = json.getLong("propertyId");
        _format = json.optString("format", null);

        if (json.has("measure"))
            _measure = json.getBoolean("measure");
        if (json.has("dimension"))
            _dimension = json.getBoolean("dimension");

        _propertyURI = json.optString("propertyURI", null);
        _rangeURI = json.optString("rangeURI", null);

        JSONArray cfs = json.getJSONArray("conditionalFormats");
        cfs.forEach(cf -> {
            JSONObject cfObj = (JSONObject) cf;
            _conditionalFormats.add(ConditionalFormat.fromJSON(cfObj));
        });

        _lookupSchema = json.optString("lookupSchema", null);
        _lookupQuery = json.optString("lookupQuery", null);
        _lookupContainer = json.optString("lookupContainer", null);
        _derivationDataScope = json.optString("derivationDataScope", null);
        _mvEnabled = json.optBoolean("mvEnabled");
    }

    public JSONObject toJSONObject()
    {
        return toJSONObject(false);
    }

    public JSONObject toJSONObject(boolean forProtocol)
    {
        JSONObject result = new JSONObject();

        if (getAllProperties() != null)
            getAllProperties().forEach(result::put);

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

        if (_derivationDataScope != null)
            result.put("derivationDataScope", _derivationDataScope);

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
            result.put("url", result.optString("URL", null));
            result.remove("URL");
            result.put("phi", result.optString("PHI", null));
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
            cfs.put(cf);
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

    public String getDerivationDataScope()
    {
        return _derivationDataScope;
    }

    public void setDerivationDataScope(String derivationDataScope)
    {
        _derivationDataScope = derivationDataScope;
    }

}
