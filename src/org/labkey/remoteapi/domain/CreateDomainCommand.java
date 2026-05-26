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

import java.util.HashMap;
import java.util.Map;

/**
 * Command class for creating a new domain.
 * <p>
 * The domain definition can be customized by modifying the {@link Domain} returned by {@link #getDomainDesign()}.
 */
public class CreateDomainCommand extends AbstractDomainUpdateCommand
{
    private String _kind;
    private Map<String, Object> _options = new HashMap<>();

    /**
     * Initialize command to create a domain of the specified "kind". Available domain kinds depends on the modules
     * installed on the target server.
     * @param kind the "kind" of domain to create (e.g. "IntList", "DataClass")
     * @param domainName name for the created domain
     */
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

    /**
     * @return the "kind" of domain to be created
     */
    public String getKind()
    {
        return _kind;
    }

    /**
     * Set the "kind" of the domain to be created.
     * @param kind the "kind" of domain to create (e.g. "IntList", "DataClass")
     */
    public void setKind(String kind)
    {
        _kind = kind;
    }

    /**
     * Get the 'options' map for the domain to be created.
     * @return domain options
     */
    public Map<String, Object> getOptions()
    {
        return _options;
    }

    /**
     * Set options for the domain to be created. Recognized options vary based on the domain kind specified.
     * @param options domain options
     */
    public void setOptions(Map<String, Object> options)
    {
        _options = options;
    }
}
