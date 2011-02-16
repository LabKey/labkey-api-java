package org.labkey.remoteapi.pipeline;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * Server response from FileNotificationCommand. Contains no return values.
 * User: jeckels
 * Date: Feb 15, 2011
 */
public class FileNotificationResponse  extends CommandResponse
{
    public FileNotificationResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }
}
