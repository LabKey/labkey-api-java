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
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

/**
 * Command to delete a domain.
 */
public class DropDomainCommand extends PostCommand<CommandResponse>
{
    private String _schemaName;
    private String _queryName;

    /**
     * Instantiate command to delete the specified domain.
     * @param schemaName parent schema of the domain to delete
     * @param queryName name of the domain to delete
     */
    public DropDomainCommand(String schemaName, String queryName)
    {
        super("property", "deleteDomain");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    /**
     * @return Name of the domain to delete
     */
    public String getQueryName()
    {
        return _queryName;
    }

    /**
     * Set the target domain's name
     * @param queryName Name of the domain to delete
     */
    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    /**
     * @return parent schema of the domain to delete
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Set the target domain's schema name
     * @param schemaName parent schema of the domain to delete
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("queryName", getQueryName());

        return json;
    }
}
