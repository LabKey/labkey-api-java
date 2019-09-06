package org.labkey.remoteapi.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateDomainCommand extends DomainCommand
{
    private String _kind;
    private Map<String, Object> _options = new HashMap<>();

    public CreateDomainCommand(String kind, String domainName)
    {
        super("property", "createDomain");
        _kind = kind;

        setDomainName(domainName);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();

        result.put("kind", getKind());
        result.put("options", _options);

        JSONArray fields = new JSONArray();
        fields.addAll(getColumns());

        JSONObject domainDesign = new JSONObject();
        domainDesign.put("name", getDomainName());
        domainDesign.put("fields", fields);

        result.put("domainDesign", domainDesign);

        if (!getOptions().isEmpty())
        {
            JSONObject domainOptions = new JSONObject();
            domainOptions.putAll(getOptions());
            result.put("options", domainOptions);
        }

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

}
