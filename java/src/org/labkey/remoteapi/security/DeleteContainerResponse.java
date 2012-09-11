package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * Response from a DeleteContainerResponse. Contains no real response data, just indicates if the delete was successful or not.
 * User: jeckels
 * Date: 9/11/12
 */
public class DeleteContainerResponse extends CommandResponse
{
    public DeleteContainerResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }
}
