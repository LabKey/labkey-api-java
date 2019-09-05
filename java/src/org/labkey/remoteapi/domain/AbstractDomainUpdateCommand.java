package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

public abstract class AbstractDomainUpdateCommand extends PostCommand<DomainResponse>
{
    private Domain _design = new Domain();

    public AbstractDomainUpdateCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    @Override
    protected DomainResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new DomainResponse(text, status, contentType, json, this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        result.put("domainDesign", _design.toJSONObject());

        return result;
    }

    public Domain getDomainDesign()
    {
        return _design;
    }

    public void setDomainDesign(Domain design)
    {
        _design = design;
    }
}
