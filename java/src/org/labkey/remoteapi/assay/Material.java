package org.labkey.remoteapi.assay;

import org.json.simple.JSONObject;

public class Material extends ExpObject
{
    private String _sampleSetName;
    private Integer _sampleSetId;

    public Material()
    {
        super();
    }

    public Material(JSONObject json)
    {
        super(json);

        if (json.containsKey("sampleSet"))
        {
            JSONObject sampleSet = (JSONObject)json.get("sampleSet");

            _sampleSetName = (String)sampleSet.get("id");
            _sampleSetId = (Integer) sampleSet.get("name");
        }
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();

        if (_sampleSetName != null || _sampleSetId != null)
        {
            JSONObject sampleSet = new JSONObject();

            if (_sampleSetName != null)
                sampleSet.put("name", _sampleSetName);
            if (_sampleSetId != null)
                sampleSet.put("id", _sampleSetId);

            result.put("sampleSet", sampleSet);
        }
        return result;
    }

    public String getSampleSetName()
    {
        return _sampleSetName;
    }

    public void setSampleSetName(String sampleSetName)
    {
        _sampleSetName = sampleSetName;
    }

    public Integer getSampleSetId()
    {
        return _sampleSetId;
    }

    public void setSampleSetId(Integer sampleSetId)
    {
        _sampleSetId = sampleSetId;
    }
}
