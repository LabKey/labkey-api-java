package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class RunTransformResponse extends CommandResponse
{
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
    public RunTransformResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    public String getJobId()
    {
        return getProperty("jobId");
    }

    public String getPipelineURL()
    {
        return getProperty("pipelineURL");
    }

    public String getStatus()
    {
        return getProperty("status");
    }

    public String getSuccess()
    {
        return getProperty("success");
    }
}
