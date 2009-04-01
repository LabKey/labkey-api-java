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
package org.labkey.remoteapi.security;

import org.labkey.remoteapi.CommandResponse;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.List;

/*
* User: Dave
* Date: Oct 27, 2008
* Time: 1:27:31 PM
*/
public class GetContainersResponse extends CommandResponse
{
    public GetContainersResponse(String text, int statusCode, String contentType, JSONObject json, GetContainersCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    public String getContainerId()
    {
        return getProperty("id");
    }

    public Integer getUserPermissions()
    {
        return getUserPermissions(null);
    }

    public Integer getUserPermissions(String containerPath)
    {
        Map<String,Object> containerInfo = findContainer(containerPath, getParsedData());
        return (null == containerInfo) ? null : ((Number)containerInfo.get("userPermissions")).intValue();
    }

    public Boolean hasPermission(int perm)
    {
        Integer userPerms = getUserPermissions();
        return (null == userPerms) ? null : userPerms == (userPerms | perm);
    }

    public Boolean hasPermission(int perm, String containerPath)
    {
        Integer userPerms = getUserPermissions(containerPath);
        return (null == userPerms) ? null : userPerms == (userPerms | perm);
    }

    @SuppressWarnings("unchecked")
    protected Map<String,Object> findContainer(String containerPath, Map<String,Object> root)
    {
        if(null == containerPath)
            return root; //assumed by code above

        containerPath = containerPath.replace('\\', '/');
        if(containerPath.startsWith("/"))
            containerPath = containerPath.substring(1);
        String[] pathParts = containerPath.split("/");

        return findContainer(pathParts, 0, root);
    }

    @SuppressWarnings("unchecked")
    protected Map<String,Object> findContainer(String[] containerPath, int index, Map<String,Object> root)
    {
        //if current root name doesn't match this path part, return null
        if(!containerPath[index].equalsIgnoreCase((String)root.get("name")))
            return null;

        //if this is the last path part, return root
        if(containerPath.length - 1 == index)
            return root;

        List<Map<String,Object>> children = (List<Map<String,Object>>)root.get("children");
        Map<String,Object> child = (null == children) ? null : findObject(children, "name", containerPath[index + 1]);
        return (null == child) ? null : findContainer(containerPath, index + 1, child);
    }

}