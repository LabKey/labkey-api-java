package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.internal.ResponseUtils;

import java.util.Map;

public class DomainDetailsResponse extends CommandResponse
{
    private final Domain _domain;
    private final String _kind;
    private final Map<String, Object> _options;

    public DomainDetailsResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _domain = new Domain(json.getJSONObject("domainDesign"));
        _kind = json.optString("domainKindName", null);
        _options = ResponseUtils.deepUnmodifiableMap(json.optJSONObject("options"));
    }

    /**
     * Get the Domain deserialized from the server's response
     * @return Domain definition
     */
    public Domain getDomain()
    {
        return _domain;
    }

    /**
     * @return domain kind name returned from the server
     */
    public String getKind()
    {
        return _kind;
    }

    /**
     * @return Domain options returned from the server
     */
    public Map<String, Object> getOptions()
    {
        return _options;
    }
}
