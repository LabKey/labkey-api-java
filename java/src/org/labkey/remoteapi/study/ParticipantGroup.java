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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A client-side representation of a participant group from a study on the server.
 * User: jeckels
 * Date: 12/11/13
 */
public class ParticipantGroup
{
    private Integer _rowId;
    private String _label;
    private String _description;
    private Set<String> _participantIds;

    public ParticipantGroup()
    {
    }

    /* package */ ParticipantGroup(Map<String, Object> groupJSON)
    {
        setRowId(((Number) groupJSON.get("rowId")).intValue());
        setParticipantIds(new HashSet<String>((Collection<String>) groupJSON.get("participantIds")));
        setLabel((String)groupJSON.get("label"));
        setDescription((String)groupJSON.get("description"));
    }

    /** @return the primary key value for the group */
    public Integer getRowId()
    {
        return _rowId;
    }

    /** @param rowId the primary key value for the group */
    public void setRowId(Integer rowId)
    {
        _rowId = rowId;
    }

    /** @return the ids of the members of the group */
    public Set<String> getParticipantIds()
    {
        return _participantIds;
    }

    /** @param participantIds the ids of the members of the group */
    public void setParticipantIds(Set<String> participantIds)
    {
        _participantIds = participantIds;
    }

    /** @return a short name for the group */
    public String getLabel()
    {
        return _label;
    }

    /** @param label a short name for the group */
    public void setLabel(String label)
    {
        _label = label;
    }

    /** @return a more verbose description of the group, compared with the label */
    public String getDescription()
    {
        return _description;
    }

    /** @param description a more verbose description of the group, compared with the label */
    public void setDescription(String description)
    {
        _description = description;
    }

    /**
     * Convert to JSON to be submitted to the server
     */
    /* package */ JSONObject toJSON()
    {
        JSONObject result = new JSONObject();
        if (_rowId != null)
        {
            result.put("rowId", _rowId);
        }
        if (_participantIds != null)
        {
            JSONArray value = new JSONArray();
            value.addAll(_participantIds);
            result.put("participantIds", value);
        }
        if (_label != null)
        {
            result.put("label", _label);
        }
        if (_description != null)
        {
            result.put("description", _description);
        }
        return result;
    }
}
