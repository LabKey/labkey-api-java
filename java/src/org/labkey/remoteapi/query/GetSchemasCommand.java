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

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.CommandException;
import org.json.simple.JSONObject;

import java.io.IOException;

/*
* User: Dave
* Date: Oct 21, 2008
* Time: 2:57:10 PM
*/

/**
 * Command to obtain the list of schemas available in a given folder path.
 */
public class GetSchemasCommand extends Command
{
    public GetSchemasCommand()
    {
        super("query", "getSchemas");
    }

    @SuppressWarnings("unchecked")
    public GetSchemasResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (GetSchemasResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new GetSchemasResponse(text, status, contentType, json);
    }
}