package org.labkey.remoteapi.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;

import java.util.ArrayList;
import java.util.List;

public class ListDomainsResponse extends CommandResponse
{
    private final List<Domain> _domains = new ArrayList<>();

    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text               The response text
     * @param statusCode         The HTTP status code
     * @param contentType        The response content type
     * @param json               The parsed JSONObject (or null if JSON was not returned)
     */
    public ListDomainsResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        JSONArray domains = json.getJSONArray("data");
        for (Object domainJSON: domains)
        {
            _domains.add(new Domain((JSONObject) domainJSON));
        }
    }

    public List<Domain> getDomains()
    {
        return _domains;
    }
}
