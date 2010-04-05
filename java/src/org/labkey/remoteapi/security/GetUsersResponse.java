/*
 * Copyright (c) 2010 LabKey Corporation
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
package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.ResponseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The result of a GetUsersCommand request.
 * User: jeckels
 * Date: Jan 26, 2010
 */
public class GetUsersResponse extends CommandResponse
{
    public GetUsersResponse(String text, int statusCode, String contentType, JSONObject json, GetUsersCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /** Information about a single user that matched the request */
    public static class UserInfo extends ResponseObject
    {
        private final int _userId;
        private final String _displayName;
        private final String _email;

        private UserInfo(Map<String, Object> map)
        {
            super(map);
            _userId = ((Number)map.get("userId")).intValue();
            _displayName = (String)map.get("displayName");
            _email = (String)map.get("email");
        }

        /** @return the display name for the user */
        public String getDisplayName()
        {
            return _displayName;
        }

        /** @return the user's email address */
        public String getEmail()
        {
            return _email;
        }

        /** @return the user's unique id within the server */
        public int getUserId()
        {
            return _userId;
        }
    }

    /** @return the users that matched the request */
    public List<UserInfo> getUsersInfo()
    {
        List<Map<String, Object>> usersNode = (List<Map<String, Object>>)getParsedData().get("users");
        List<UserInfo> result = new ArrayList<UserInfo>();
        for (Map<String, Object> map : usersNode)
        {
            result.add(new UserInfo(map));
        }
        return result;
    }

    /** @return the path of the requested container */
    public String getContainerPath()
    {
        return (String)getParsedData().get("container");
    }
}
