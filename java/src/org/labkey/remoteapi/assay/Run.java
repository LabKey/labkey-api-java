/*
 * Copyright (c) 2010-2014 LabKey Corporation
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    private List<Data> _dataInputs = new ArrayList<Data>();
    private List<Data> _dataOutputs = new ArrayList<Data>();

    // Used when inserting new data via SaveAssayBatchAction
    private List<Map<String, Object>> _resultData;

    public Run()
    {
        super();
    }

    public Run(JSONObject json)
    {
        super(json);

        if (json.containsKey("dataInputs"))
        {
            JSONArray array = (JSONArray)json.get("dataInputs");
            for (Object o : array)
            {
                _dataInputs.add(new Data((JSONObject) o));
            }
        }

        if (json.containsKey("dataOutputs"))
        {
            JSONArray array = (JSONArray)json.get("dataOutputs");
            for (Object o : array)
            {
                _dataOutputs.add(new Data((JSONObject) o));
            }
        }

        _comment = (String)json.get("comment");
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();
        JSONArray dataInputs = new JSONArray();
        for (Data data : _dataInputs)
        {
            dataInputs.add(data.toJSONObject());
        }
        result.put("dataInputs", dataInputs);

        JSONArray dataOutputs = new JSONArray();
        for (Data data : _dataOutputs)
        {
            dataOutputs.add(data.toJSONObject());
        }
        result.put("dataOutputs", dataOutputs);
        result.put("comment", _comment);

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
                dataRows.add(o);
            }
            result.put("dataRows", dataRows);
        }

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
}
