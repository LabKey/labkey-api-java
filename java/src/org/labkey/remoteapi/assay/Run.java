package org.labkey.remoteapi.assay;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
}
