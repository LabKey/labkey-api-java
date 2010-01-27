package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;

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
    public static class UserInfo
    {
        private final int _userId;
        private final String _displayName;
        private final String _email;
        private final Map<String, Object> _allProperties;

        private UserInfo(Map<String, Object> map)
        {
            _allProperties = map;
            _userId = ((Number)map.get("userId")).intValue();
            _displayName = (String)map.get("displayName");
            _email = (String)map.get("email");
        }

        /** @return the full set of properties returned by the server */
        public Map<String, Object> getAllProperties()
        {
            return _allProperties;
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
