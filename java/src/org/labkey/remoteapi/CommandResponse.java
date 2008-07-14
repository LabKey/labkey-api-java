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
package org.labkey.remoteapi;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.List;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 1:21:24 PM
*/

/**
 * Contains the details about the response sent by the server after executing a command
 */
public class CommandResponse
{
    private String _text;
    private int _statusCode;
    private JSONObject _data = null;

    public CommandResponse(String text, int statusCode)
    {
        _text = text;
        _statusCode = statusCode;
    }

    public String getText()
    {
        return _text;
    }

    public int getStatusCode()
    {
        return _statusCode;
    }

    @SuppressWarnings("unchecked")
    public Map<String,Object> getParsedData()
    {
        if(null == _data && null != getText())
            _data = (JSONObject)JSONValue.parse(getText());
        return _data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String path)
    {
        assert null != path;
        String[] pathParts = path.split("\\.");
        if(null == pathParts || pathParts.length == 0)
            return null;
        return (T)getProperty(pathParts, 0, getParsedData());
    }

    @SuppressWarnings("unchecked")
    protected <T> T getProperty(String[] path, int index, Map<String,Object> parent)
    {
        Object prop = parent.get(path[index]);
        //if this was the last path part, return the prop
        //will return null if final path part not found
        if(index == (path.length -1))
            return (T)prop;
        else
        {
            //recurse if prop is non-null and instance of map
            return (null != prop && prop instanceof Map)
                ? (T)getProperty(path, index + 1, (Map<String,Object>)prop)
                : null;
        }
    }

    protected Map<String,Object> findObject(List<Map<String,Object>> objects, String propertyName, String value)
    {
        assert null != value;
        for(Map<String,Object> obj : objects)
        {
            if(value.equalsIgnoreCase(String.valueOf(obj.get(propertyName))))
                return obj;
        }
        return null;
    }
}