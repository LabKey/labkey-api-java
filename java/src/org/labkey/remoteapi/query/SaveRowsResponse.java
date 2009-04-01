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
package org.labkey.remoteapi.query;

import org.json.simple.JSONObject;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 1:16:58 PM
*/
/**
 * Response object used for commands that derive from SaveRowsCommand.
 * This response object provides helper methods for accessing the important
 * bits of the parsed response data.
 */
public class SaveRowsResponse extends RowsResponse
{
    /**
     * Constructs a new SaveRowsResponse given the response text and status code
     * @param text The response text.
     * @param statusCode The HTTP status code.
     * @param contentType The Content-Type header value.
     * @param json The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public SaveRowsResponse(String text, int statusCode, String contentType, JSONObject json, SaveRowsCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     * Returns the 'rowsAffected' response property.
     * @return The number of rows affected by the command, or null if this property
     * was not present in the response.
     */
    public Number getRowsAffected()
    {
        return getProperty("rowsAffected");
    }

    /**
     * Returns the 'schemaName' response property.
     * @return The schema name affected by the command, or null if this property
     * was not present in the response.
     */
    public String getSchemaName()
    {
        return getProperty("schemaName");
    }

    /**
     * Returns the 'queryName' response property.
     * @return The query name affected by the command, or null if this property
     * was not present in the response.
     */
    public String getQueryName()
    {
        return getProperty("queryName");
    }

    /**
     * Returns the 'command' response property.
     * @return The command executed, or null if this property
     * was not present in the response.
     */
    public String getCommand()
    {
        return getProperty("command");
    }
}