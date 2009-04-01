/*
 * Copyright (c) 2008-2009 LabKey Corporation
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
package org.labkey.remoteapi.assay;

import org.labkey.remoteapi.CommandResponse;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 1:59:49 PM
*/
/**
 * Response class for the {@link AssayListCommand}. This class
 * provides helpful getter method to access particular bits of the parsed
 * response data.
 */
public class AssayListResponse extends CommandResponse
{
    /**
     * Constructs a new AssayListResponse
     * @param text The response text.
     * @param statusCode The HTTP status code.
     * @param contentType The HTTP response content type
     * @param json The parsed JSONObject (or null if JSON was not returned)
     * @param sourceCommand A copy of the command that created this response
     */
    public AssayListResponse(String text, int statusCode, String contentType, JSONObject json, AssayListCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     * Returns the definitions section of the response. This returns
     * a list of assay definition Maps, each of which contains properties
     * about the particular assay definition.
     * @return The list of definitions.
     */
    public List<Map<String,Object>> getDefinitions()
    {
        return getProperty("definitions");
    }

    /**
     * Returns an assay definition for the assay identified by the specified name.
     * @param name The name of the assay definition to find.
     * @return The assay definition or null if not found.
     */
    public Map<String,Object> getDefinition(String name)
    {
        return findObject(getDefinitions(), "name", name);
    }

    /**
     * Returns an assay definition for the assay identified by the specified id.
     * @param id The id of the assay definition to find.
     * @return The assay definition or null if not found.
     */
    public Map<String,Object> getDefinition(int id)
    {
        return findObject(getDefinitions(), "id", String.valueOf(id));
    }
}