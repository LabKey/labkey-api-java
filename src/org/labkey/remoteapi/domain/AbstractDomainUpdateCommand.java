/*
 * Copyright (c) 2019-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        return new DomainResponse(text, status, contentType, json);
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
