package org.labkey.remoteapi.assay;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* User: jeckels
* Date: Apr 28, 2010
*/
public class Batch extends ExpObject
{
    private List<Run> _runs = new ArrayList<Run>();

    public Batch()
    {
        super();
    }

    public Batch(JSONObject json)
    {
        super(json);
        if (json.containsKey("runs"))
        {
            JSONArray array = (JSONArray)json.get("runs");
            for (Object o : array)
            {
                JSONObject run = (JSONObject) o;
                _runs.add(new Run(run));
            }
        }
    }

    public List<Run> getRuns()
    {
        return _runs;
    }

    public void setRuns(List<Run> runs)
    {
        _runs = runs;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.add(run.toJSONObject());
        }
        result.put("runs", runs);
        return result;
    }
}
