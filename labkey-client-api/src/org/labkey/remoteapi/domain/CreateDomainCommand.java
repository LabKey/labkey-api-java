package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateDomainCommand extends AbstractDomainUpdateCommand
{
    private String _kind;
    private Map<String, Object> _options = new HashMap<>();

    public CreateDomainCommand(String kind, String domainName)
    {
        super("property", "createDomain");
        _kind = kind;

        getDomainDesign().setName(domainName);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();

        result.put("kind", _kind);
        result.put("options", _options);

        return result;
    }

    public String getKind()
    {
        return _kind;
    }

    public void setKind(String kind)
    {
        _kind = kind;
    }

    public Map<String, Object> getOptions()
    {
        return _options;
    }

    public void setOptions(Map<String, Object> options)
    {
        _options = options;
    }
}
