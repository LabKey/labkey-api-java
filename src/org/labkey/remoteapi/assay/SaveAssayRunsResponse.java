package org.labkey.remoteapi.assay;

import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.HasRequiredVersion;

import java.util.ArrayList;
import java.util.List;

public class SaveAssayRunsResponse extends CommandResponse
{
    private List<Run> _runs = new ArrayList<>();

    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text               The response text
     * @param statusCode         The HTTP status code
     * @param contentType        The response content type
     * @param json               The parsed JSONObject (or null if JSON was not returned)
     * @param hasRequiredVersion An object that implements HasRequiredVersion
     */
    public SaveAssayRunsResponse(String text, int statusCode, String contentType, JSONObject json, HasRequiredVersion hasRequiredVersion)
    {
        super(text, statusCode, contentType, json, hasRequiredVersion);
        JSONArray array = json.getJSONArray("runs");
        for (Object o : array)
        {
            JSONObject run = (JSONObject) o;
            _runs.add(new Run(run));
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
}
