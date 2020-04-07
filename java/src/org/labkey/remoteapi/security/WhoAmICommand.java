package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

public class WhoAmICommand extends Command<WhoAmIResponse>
{
    public WhoAmICommand()
    {
        super("login", "whoami.api");
    }

    @Override
    protected WhoAmIResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new WhoAmIResponse(text, status, contentType, json, this);
    }
}
