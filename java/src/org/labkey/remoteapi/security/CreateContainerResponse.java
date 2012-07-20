package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: jeckels
 * Date: Jul 20, 2012
 */
public class CreateContainerResponse extends CommandResponse
{
    public CreateContainerResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }
}
