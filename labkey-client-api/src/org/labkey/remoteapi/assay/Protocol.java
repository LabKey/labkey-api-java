package org.labkey.remoteapi.assay;

import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.ResponseObject;
import org.labkey.remoteapi.domain.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Protocol extends ResponseObject
{
    private Integer _protocolId;
    private String _name;
    private String _description;
    private String _providerName;
    private List<Domain> _domains = new ArrayList<>();

    private Boolean _allowBackgroundUpload;

    private Boolean _allowSpacesInPath;
    private Boolean _allowTransformationScript;
    private Boolean _backgroundUpload;

    private Boolean _allowEditableResults;
    private Boolean _editableResults;
    private Boolean _editableRuns;
    private Boolean _saveScriptFiles;

    private Boolean _allowQCStates;
    private Boolean _qcEnabled;

    private String _autoCopyTargetContainerId;

    private List<String> _availableDetectionMethods;
    private String _selectedDetectionMethod;

    private Map<String, String> _availableMetadataInputFormats;
    private String _selectedMetadataInputFormat;

    private List<String> _availablePlateTemplates;
    private String _selectedPlateTemplate;

    private Map<String, String> _protocolParameters;
    private List<String> _protocolTransformScripts;

    public Protocol()
    {
        super(null);
    }

    public Protocol(JSONObject json)
    {
        super(json.toMap());

        _protocolId = (Integer)json.optNumber("protocolId", null);
        _name = (String)json.get("name");
        _description = json.optString("description", null);
        _providerName = (String)json.get("providerName");

        if (json.get("domains") instanceof JSONArray)
        {
            for (Object domain : json.getJSONArray("domains"))
                _domains.add(new Domain((JSONObject)domain));
        }

        if (json.has("allowBackgroundUpload"))
            _allowBackgroundUpload = (Boolean)json.get("allowBackgroundUpload");
        if (json.has("backgroundUpload"))
            _backgroundUpload = (Boolean)json.get("backgroundUpload");
        if (json.has("allowEditableResults"))
            _allowEditableResults = (Boolean)json.get("allowEditableResults");
        if (json.has("editableResults"))
            _editableResults = (Boolean)json.get("editableResults");
        if (json.has("editableRuns"))
            _editableRuns = (Boolean)json.get("editableRuns");
        if (json.has("saveScriptFiles"))
            _saveScriptFiles = (Boolean)json.get("saveScriptFiles");
        if (json.has("qcEnabled"))
            _qcEnabled = (Boolean)json.get("qcEnabled");
        if (json.has("allowQCStates"))
            _allowQCStates = (Boolean)json.get("allowQCStates");
        if (json.has("allowSpacesInPath"))
            _allowSpacesInPath = (Boolean)json.get("allowSpacesInPath");
        if (json.has("allowTransformationScript"))
            _allowTransformationScript = (Boolean)json.get("allowTransformationScript");
        _autoCopyTargetContainerId = json.optString("autoCopyTargetContainerId", null);

        if (json.get("availableDetectionMethods") instanceof JSONArray)
            _availableDetectionMethods = convert(json.getJSONArray("availableDetectionMethods"));
        _selectedDetectionMethod = json.optString("selectedDetectionMethod", null);
        if (json.get("availableMetadataInputFormats") instanceof JSONObject)
            _availableMetadataInputFormats = convert(json.getJSONObject("availableMetadataInputFormats"));
        if (json.has("selectedMetadataInputFormat"))
            _selectedMetadataInputFormat = (String)json.get("selectedMetadataInputFormat");
        if (json.get("availablePlateTemplates") instanceof JSONArray)
            _availablePlateTemplates = convert(json.getJSONArray("availablePlateTemplates"));
        if (json.has("selectedPlateTemplate"))
            _selectedPlateTemplate = (String)json.get("selectedPlateTemplate");

        if (json.has("protocolTransformScripts"))
            _protocolTransformScripts = convert(json.getJSONArray("protocolTransformScripts"));
        if (json.has("protocolParameters"))
            _protocolParameters = convert(json.getJSONObject("protocolParameters"));
    }

    List<String> convert(JSONArray array)
    {
        return array.toList().stream().map(Object::toString).toList();
    }

    Map<String, String> convert(JSONObject json)
    {
        Map<String, Object> map = json.toMap();
        Map<String, String> ret = new HashMap<>((int) (map.size()*1.5));
        map.forEach((k, v)->ret.put(k, v.toString()));

        return ret;
    }

    public JSONObject toJSONObject()
    {
        JSONObject result = new JSONObject();
        result.put("protocolId", _protocolId);
        result.put("name", _name);
        result.put("description", _description);
        result.put("providerName", _providerName);
        JSONArray domains = new JSONArray();
        result.put("domains", domains);
        for (Domain domain : _domains)
            domains.put(domain.toJSONObject(true));

        if (_backgroundUpload != null)
            result.put("backgroundUpload", _backgroundUpload);

        if (_editableResults != null)
            result.put("editableResults", _editableResults);

        if (_qcEnabled != null)
            result.put("qcEnabled", _qcEnabled);

        if (_protocolTransformScripts != null)
            result.put("protocolTransformScripts", _protocolTransformScripts);
        if (_saveScriptFiles != null)
            result.put("saveScriptFiles", _saveScriptFiles);
        if (_editableRuns != null)
            result.put("editableRuns", _editableRuns);
        if (_autoCopyTargetContainerId != null)
            result.put("autoCopyTargetContainerId", _autoCopyTargetContainerId);

        if (_selectedDetectionMethod != null)
            result.put("selectedDetectionMethod", _selectedDetectionMethod);

        if (_selectedMetadataInputFormat != null)
            result.put("selectedMetadataInputFormat", _selectedMetadataInputFormat);

        if (_selectedPlateTemplate != null)
            result.put("selectedPlateTemplate", _selectedPlateTemplate);

        if (_protocolParameters != null)
            result.put("protocolParameters", _protocolParameters);

        return result;
    }

    public Integer getProtocolId()
    {
        return _protocolId;
    }

    public String getName()
    {
        return _name;
    }

    public Protocol setName(String name)
    {
        _name = name;
        return this;
    }

    public String getDescription()
    {
        return _description;
    }

    public Protocol setDescription(String description)
    {
        _description = description;
        return this;
    }

    public String getProviderName()
    {
        return _providerName;
    }

    public Protocol setProviderName(String providerName)
    {
        _providerName = providerName;
        return this;
    }

    public List<Domain> getDomains()
    {
        return _domains;
    }

    public Protocol setDomains(List<Domain> domains)
    {
        _domains = domains;
        return this;
    }

    public Boolean getAllowBackgroundUpload()
    {
        return _allowBackgroundUpload;
    }

    public Boolean getAllowEditableResults()
    {
        return _allowEditableResults;
    }

    public Boolean getAllowQCStates()
    {
        return _allowQCStates;
    }

    public boolean getAllowSpacesInPath()
    {
        return _allowSpacesInPath;
    }

    public Boolean getAllowTransformationScript()
    {
        return _allowTransformationScript;
    }

    public Protocol setBackgroundUpload(Boolean backgroundUpload)
    {
        _backgroundUpload = backgroundUpload;
        return this;
    }

    public boolean getBackgroundUpload()
    {
        return _backgroundUpload;
    }

    public Protocol setEditableResults(Boolean editableResults)
    {
        _editableResults = editableResults;
        return this;
    }

    public Boolean getEditableResults()
    {
        return _editableResults;
    }

    public Protocol setEditableRuns(Boolean editableRuns)
    {
        _editableRuns = editableRuns;
        return this;
    }

    public Boolean getEditableRuns()
    {
        return _editableRuns;
    }

    public Protocol setSaveScriptFiles(Boolean saveScriptFiles)
    {
        _saveScriptFiles = saveScriptFiles;
        return this;
    }

    public Boolean getSaveScriptFiles()
    {
        return _saveScriptFiles;
    }

    public Protocol setQCEnabled(boolean qcEnabled)
    {
        _qcEnabled = qcEnabled;
        return this;
    }

    public Boolean getQcEnabled()
    {
        return _qcEnabled;
    }

    public Protocol setAutoCopyTargetContainerId(String autoCopyTargetContainerId)
    {
        _autoCopyTargetContainerId = autoCopyTargetContainerId;
        return this;
    }

    public String getAutoCopyTargetContainerId()
    {
        return _autoCopyTargetContainerId;
    }

    public List<String> getAvailableDetectionMethods()
    {
        return _availableDetectionMethods;
    }

    public Protocol setSelectedDetectionMethod(String selectedDetectionMethod)
    {
         _selectedDetectionMethod = selectedDetectionMethod;
         return this;
    }

    public String getSelectedDetectionMethod()
    {
        return _selectedDetectionMethod;
    }

    public Map<String, String> getAvailableMetadataInputFormats()
    {
        return _availableMetadataInputFormats;
    }

    public Protocol setSelectedMetadataInputFormat(String inputFormat)
    {
        _selectedMetadataInputFormat = inputFormat;
        return this;
    }

    public String getSelectedMetadataInputFormat()
    {
        return _selectedMetadataInputFormat;
    }

    public List<String> getAvailablePlateTemplates()
    {
        return _availablePlateTemplates;
    }

    public Protocol setSelectedPlateTemplate(String selectedPlateTemplate)
    {
        _selectedPlateTemplate = selectedPlateTemplate;
        return this;
    }

    public String getSelectedPlateTemplate()
    {
        return _selectedPlateTemplate;
    }

    public Protocol setProtocolParameters(Map<String, String> protocolParameters)
    {
        _protocolParameters = protocolParameters;
        return this;
    }

    public Map<String,String> getProtocolParameters()
    {
        return _protocolParameters;
    }

    public Protocol setProtocolTransformScripts(List<String> protocolTransformScripts)
    {
        _protocolTransformScripts = protocolTransformScripts;
        return this;
    }

    public List<String> getProtocolTransformScripts()
    {
        return _protocolTransformScripts;
    }
}
