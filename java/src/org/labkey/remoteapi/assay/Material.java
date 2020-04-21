/*
 * Copyright (c) 2018 LabKey Corporation
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

import org.json.simple.JSONObject;

public class Material extends ExpObject
{
    private String _sampleSetName;
    private Long _sampleSetId;

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

            _sampleSetName = (String) sampleSet.get("name");
            _sampleSetId = (Long) sampleSet.get("id");
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

    public Long getSampleSetId()
    {
        return _sampleSetId;
    }

    public void setSampleSetId(Long sampleSetId)
    {
        _sampleSetId = sampleSetId;
    }
}
