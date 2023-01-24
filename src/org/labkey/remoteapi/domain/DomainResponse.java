package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

/**
 * Response object for commands that expect the server to return a serialized domain.
 */
public class DomainResponse extends CommandResponse
{
    private final Domain _domain;

    public DomainResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _domain = new Domain(json);
    }

    /**
     * Get the Domain deserialized from the server's response
     * @return Domain definition
     */
    public Domain getDomain()
    {
        return _domain;
    }
}
