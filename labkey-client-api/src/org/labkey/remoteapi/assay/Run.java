/*
 * Copyright (c) 2010-2018 LabKey Corporation
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Represents a single assay run
* User: jeckels
* Date: Apr 28, 2010
*/
public class Run extends ExpObject
{
    private String _comment;
    private List<Data> _dataInputs = new ArrayList<>();
    private List<Data> _dataOutputs = new ArrayList<>();
    private List<Material> _materialInputs = new ArrayList<>();
    private List<Material> _materialOutputs = new ArrayList<>();
    private String _lsid;
    private JSONObject _plateMetadata;

    // Used when inserting new data via SaveAssayBatchAction
    private List<Map<String, Object>> _resultData;

    public Run()
    {
        super();
    }

    public Run(JSONObject json)
    {
        super(json);

        if (json.has("dataInputs"))
        {
            JSONArray array = json.getJSONArray("dataInputs");
            for (Object o : array)
            {
                _dataInputs.add(new Data((JSONObject) o));
            }
        }

        if (json.has("dataOutputs"))
        {
            JSONArray array = json.getJSONArray("dataOutputs");
            for (Object o : array)
            {
                _dataOutputs.add(new Data((JSONObject) o));
            }
        }

        if (json.has("materialInputs"))
        {
            JSONArray array = json.getJSONArray("materialInputs");
            for (Object o : array)
            {
                _materialInputs.add(new Material((JSONObject) o));
            }
        }

        if (json.has("materialOutputs"))
        {
            JSONArray array = json.getJSONArray("materialOutputs");
            for (Object o : array)
            {
                _materialOutputs.add(new Material((JSONObject) o));
            }
        }
        _comment = (String)json.get("comment");

        if (json.has("lsid"))
        {
            _lsid = (String) json.get("lsid");
        }

        if (json.has("plateMetadata"))
        {
            _plateMetadata = (JSONObject)json.get("plateMetadata");
        }
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();
        JSONArray dataInputs = new JSONArray();
        for (Data data : _dataInputs)
        {
            dataInputs.put(data.toJSONObject());
        }
        result.put("dataInputs", dataInputs);

        JSONArray dataOutputs = new JSONArray();
        for (Data data : _dataOutputs)
        {
            dataOutputs.put(data.toJSONObject());
        }
        result.put("dataOutputs", dataOutputs);
        result.put("comment", _comment);
        result.put("lsid", _lsid);

        JSONArray materialInputs = new JSONArray();
        JSONArray materialOutputs = new JSONArray();

        for (Material material : _materialInputs)
        {
            materialInputs.put(material.toJSONObject());
        }
        result.put("materialInputs", materialInputs);

        for (Material material : _materialOutputs)
        {
            materialOutputs.put(material.toJSONObject());
        }
        result.put("materialOutputs", materialOutputs);

        if (_resultData != null && !_resultData.isEmpty())
        {
            JSONArray dataRows = new JSONArray();
            for (Map<String, Object> row : _resultData)
            {
                JSONObject o = new JSONObject();
                for (Map.Entry<String, Object> entry : row.entrySet())
                {
                    o.put(entry.getKey(), entry.getValue());
                }
                dataRows.put(o);
            }
            result.put("dataRows", dataRows);
        }

        if (_plateMetadata != null)
            result.put("plateMetadata", _plateMetadata);

        return result;
    }

    /** @return the list of data files that were consumed by this run */
    public List<Data> getDataInputs()
    {
        return _dataInputs;
    }

    /** @param dataInputs the list of data files that were consumed by this run */
    public void setDataInputs(List<Data> dataInputs)
    {
        _dataInputs = dataInputs;
    }

    /** @return the list of data files that were produced by this run */
    public List<Data> getDataOutputs()
    {
        return _dataOutputs;
    }

    /** @param dataOutputs the list of data files that were produced by this run */
    public void setDataOutputs(List<Data> dataOutputs)
    {
        _dataOutputs = dataOutputs;
    }

    public List<Material> getMaterialInputs()
    {
        return _materialInputs;
    }

    public void setMaterialInputs(List<Material> materialInputs)
    {
        _materialInputs = materialInputs;
    }

    public List<Material> getMaterialOutputs()
    {
        return _materialOutputs;
    }

    public void setMaterialOutputs(List<Material> materialOutputs)
    {
        _materialOutputs = materialOutputs;
    }

    /** @return the free-form comment attached to this run */
    public String getComment()
    {
        return _comment;
    }

    /** @param comment the free-form comment attached to this run */
    public void setComment(String comment)
    {
        _comment = comment;
    }

    /** @param resultData the list of data rows in this assay run */
    public void setResultData(List<Map<String, Object>> resultData)
    {
        _resultData = resultData;
    }

    public String getLsid()
    {
        return _lsid;
    }

    public void setLsid(String lsid)
    {
        _lsid = lsid;
    }

    public JSONObject getPlateMetadata()
    {
        return _plateMetadata;
    }

    public void setPlateMetadata(JSONObject plateMetadata)
    {
        _plateMetadata = plateMetadata;
    }
}
