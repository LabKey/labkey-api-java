/*
 * Copyright (c) 2008 LabKey Corporation
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
package org.labkey.remoteapi.query;

import org.labkey.remoteapi.CommandResponse;
import org.json.simple.JSONObject;

import java.util.List;

/*
* User: Dave
* Date: Oct 21, 2008
* Time: 2:58:17 PM
*/
/**
 * Represents the response of a GetSchemasCommand.
 */
public class GetSchemasResponse extends CommandResponse
{
    public GetSchemasResponse(String text, int statusCode, String contentType, JSONObject json, double requiredVersion)
    {
        super(text, statusCode, contentType, json, requiredVersion);
    }

    /**
     * Returns the list of schema names available in the folder path in which this command was executed.
     * @return The list of available schema names.
     */
    public List<String> getSchemaNames()
    {
        return getProperty("schemas");
    }
}