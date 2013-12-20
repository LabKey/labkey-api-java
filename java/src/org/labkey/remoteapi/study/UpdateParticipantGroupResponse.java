/*
 * Copyright (c) 2013 LabKey Corporation
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
package org.labkey.remoteapi.study;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

import java.util.Map;

/**
 * Result of making a request to the server to update an existing participant group
 * User: jeckels
 * Date: 12/11/13
 */
public class UpdateParticipantGroupResponse extends CommandResponse
{
    public UpdateParticipantGroupResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    public ParticipantGroup getParticipantGroup()
    {
        Map<String, Object> parsedData = getParsedData();
        Map<String, Object> groupJSON = (Map<String, Object>) parsedData.get("group");
        return new ParticipantGroup(groupJSON);
    }
}
