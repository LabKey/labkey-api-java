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
