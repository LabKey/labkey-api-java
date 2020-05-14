package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

public class WhoAmIResponse extends CommandResponse
{
    public WhoAmIResponse(String text, int statusCode, String contentType, JSONObject json, WhoAmICommand cmd)
    {
        super(text, statusCode, contentType, json, cmd);
    }

    public Number getUserId()
    {
        return getProperty("id");
    }

    public String getEmail()
    {
        return getProperty("email");
    }

    public String getDisplayName()
    {
        return getProperty("displayName");
    }

    public boolean isImpersonated()
    {
        return (Boolean)getProperty("impersonated");
    }

    public String getCSRF()
    {
        return getProperty("CSRF");
    }
}
