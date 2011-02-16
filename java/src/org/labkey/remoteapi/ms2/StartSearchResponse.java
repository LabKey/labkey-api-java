package org.labkey.remoteapi.ms2;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: jeckels
 * Date: Feb 11, 2011
 */
public class StartSearchResponse extends CommandResponse
{
    public StartSearchResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }
}
