package org.labkey.remoteapi.security;

import org.json.JSONObject;
import org.labkey.remoteapi.GetCommand;

public class WhoAmICommand extends GetCommand<WhoAmIResponse>
{
    public WhoAmICommand()
    {
        super("login", "whoami.api");
    }

    @Override
    protected WhoAmIResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new WhoAmIResponse(text, status, contentType, json);
    }
}
