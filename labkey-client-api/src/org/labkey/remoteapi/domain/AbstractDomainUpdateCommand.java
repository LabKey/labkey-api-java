package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Base class for shared functionality of {@link SaveDomainCommand} and {@link CreateDomainCommand}
 */
abstract class AbstractDomainUpdateCommand extends PostCommand<DomainResponse>
{
    private Domain _design = new Domain();

    AbstractDomainUpdateCommand(String controllerName, String actionName)
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

    /**
     * Get the domain design that will be POSTed by this command. The design is mutable.
     * @return reference to the domain design
     */
    public Domain getDomainDesign()
    {
        return _design;
    }

    /**
     * Set the domain design that will be POSTed by this command.
     * @param design domain design to be used by this command.
     */
    public void setDomainDesign(Domain design)
    {
        _design = design;
    }
}
