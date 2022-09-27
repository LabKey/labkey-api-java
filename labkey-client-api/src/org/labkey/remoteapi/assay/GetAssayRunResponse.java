package org.labkey.remoteapi.assay;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

public class GetAssayRunResponse extends CommandResponse
{
    private Run _run;

    public Run getRun()
    {
        return _run;
    }

    public void setRun(Run run)
    {
        _run = run;
    }

    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text          The response text
     * @param statusCode    The HTTP status code
     * @param contentType   The response content type
     * @param json          The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public GetAssayRunResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        JSONObject runJson = (JSONObject) json.get("run");
        _run = new Run(runJson);
    }
}
