package org.labkey.remoteapi.security;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

public class WhoAmIResponse extends CommandResponse
{
    public WhoAmIResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
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
        return getProperty("impersonated");
    }

    public String getCSRF()
    {
        return getProperty("CSRF");
    }
}
