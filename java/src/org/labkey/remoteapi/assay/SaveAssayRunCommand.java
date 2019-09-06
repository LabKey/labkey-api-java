package org.labkey.remoteapi.assay;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves runs on the server.
 */
public class SaveAssayRunCommand extends PostCommand<SaveAssayRunResponse>
{
    private List<Run> _runs = new ArrayList<Run>();

    /**
     * @param runs the runs to be saved
     */
    public SaveAssayRunCommand(List<Run> runs)
    {
        super("assay", "saveAssayRun");
        _runs = runs;
    }

    public SaveAssayRunCommand(SaveAssayRunCommand source)
    {
        super(source);
        _runs = source._runs;
    }

    @Override
    public PostCommand copy()
    {
        return new SaveAssayRunCommand(this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.add(run.toJSONObject());
        }
        result.put("runs", runs);
        return result;
    }
}
