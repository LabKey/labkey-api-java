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
import org.labkey.remoteapi.PostCommand;

import java.util.Set;

/**
 * Request to be sent to the server to update an existing participant group
 * User: jeckels
 * Date: 12/11/13
 */
public class UpdateParticipantGroupCommand extends PostCommand<UpdateParticipantGroupResponse>
{
    /** Group to be updated, including modifications */
    private ParticipantGroup _group;

    /**
     * Special subclass that allows specifying just participants that should be added
     * or removed, without needing to specify the full set of participants.
     */
    public static class UpdatedParticipantGroup extends ParticipantGroup
    {
        private Set<String> _ensureParticipantIds;
        private Set<String> _deleteParticipantIds;

        /** @return the set of participants that will be added to the group if they are not already members */
        public Set<String> getEnsureParticipantIds()
        {
            return _ensureParticipantIds;
        }

        /** @param ensureParticipantIds the set of participants that should be added to the group if they are not already members */
        public void setEnsureParticipantIds(Set<String> ensureParticipantIds)
        {
            _ensureParticipantIds = ensureParticipantIds;
        }

        /** @return the set of participants that will be removed to the group if they are already members */
        public Set<String> getDeleteParticipantIds()
        {
            return _deleteParticipantIds;
        }

        /** @param deleteParticipantIds the set of participants that should be removed from the group if they are currently members */
        public void setDeleteParticipantIds(Set<String> deleteParticipantIds)
        {
            _deleteParticipantIds = deleteParticipantIds;
        }

        @Override
        JSONObject toJSON()
        {
            JSONObject result = super.toJSON();
            if (_ensureParticipantIds != null)
            {
                JSONArray value = new JSONArray();
                value.addAll(_ensureParticipantIds);
                result.put("ensureParticipantIds", value);
            }
            if (_deleteParticipantIds != null)
            {
                JSONArray value = new JSONArray();
                value.addAll(_deleteParticipantIds);
                result.put("deleteParticipantIds", value);
            }

            return result;
        }
    }

    /**
     * @param group the group, including its requested updates.
     * If fields are not set, they will not be changed from their existing values in the current saved group.
     */
    public UpdateParticipantGroupCommand(ParticipantGroup group)
    {
        super("participant-group", "updateParticipantGroup");
        _group = group;
    }

    @Override
    public JSONObject getJsonObject()
    {
        return _group.toJSON();
    }

    @Override
    protected UpdateParticipantGroupResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new UpdateParticipantGroupResponse(text, status, contentType, json, this);
    }
}
