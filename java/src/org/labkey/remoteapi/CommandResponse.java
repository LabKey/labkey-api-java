/*
 * Copyright (c) 2008-2013 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 1:21:24 PM
*/

/**
 * Represents the details of a response returned from the LabKey Server.
 * <p>
 * Client code will typically use a class derived from this class that is
 * returned from the particular command used (e.g. SelectRowsCommand returns
 * SelectRowsResponse). However, if you are using the Command class directly to
 * call an HTTP API that does not yet have a specialized command class,
 * you would use this object to obtain the response details.
 *
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class CommandResponse
{
    private String _text;
    private int _statusCode;
    private String _contentType;
    private JSONObject _data = null;
    private Command _sourceCommand;

    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     * @param text The response text
     * @param statusCode The HTTP status code
     * @param contentType The response content type
     * @param json The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public CommandResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        _text = text;
        _statusCode = statusCode;
        _contentType = contentType;
        _data = json;
        _sourceCommand = sourceCommand;
    }

    /**
     * Returns the raw response text.
     * <p>
     * Use this if your command
     * returns something other than JSON, or if you want to
     * use a different JSON parser.
     * <p>
     * This may return null if no
     * response body was sent by the server.
     * @return The raw response text.
     */
    public String getText()
    {
        return _text;
    }

    /**
     * Returns the HTTP status code (typically 200).
     * @return The HTTP status code
     */
    public int getStatusCode()
    {
        return _statusCode;
    }

    /**
     * Returns the Content-Type header value from the response.
     * Note that this may return null if no content type header was
     * supplied by the server (unlikely but possible).
     * @return The content type of the response.
     */
    public String getContentType()
    {
        return _contentType;
    }

    /**
     * Returns the API version number required by the source command.
     * Some APIs may return data in a different format depending on the required version
     * @return the requried API version number
     */
    public double getRequiredVersion()
    {
        return _sourceCommand.getRequiredVersion();
    }

    /**
     * Returns a reference to a copy of the command that created this response
     * @return The command that created this response
     */
    public Command getSourceCommand()
    {
        return _sourceCommand;
    }

    /**
     * Attempts to parse the response text and return a property Map.
     * <p>
     * If the response text cannot be parsed, a runtime error will be thrown.
     * <p>
     * Note that the values in the Map may be simple values,
     * Lists, or Maps.
     * @return The parsed data as a property map.
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getParsedData()
    {
        if(null == _data && null != getText()
                && null != _contentType && _contentType.contains(Command.CONTENT_TYPE_JSON))
            _data = (JSONObject)JSONValue.parse(getText());
        return _data;
    }

    /**
     * Returns the value of a specific property in the parsed data
     * given a path to that property.
     * <p>
     * The path is a period-delimited list of property names. For
     * example, to obtain the 'bar' property from the Map associated
     * with the 'foo' property, the path would be 'foo.bar'.
     * @param path The property path.
     * @return The property value, or null if the property was not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String path)
    {
        assert null != path;
        String[] pathParts = path.split("\\.");
        if(null == pathParts || pathParts.length == 0)
            return null;
        return (T)getProperty(pathParts, 0, getParsedData());
    }

    /**
     * Called by {@link #getProperty(String)} after splitting the path into
     * a String[], and recursively by itself as it descends the property
     * hierarchy.
     * @param path The path split into a String[].
     * @param index The current index into the path array.
     * @param parent The current parent map.
     * @return The property value, or null if not found.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getProperty(String[] path, int index, Map<String,Object> parent)
    {
        if(null == parent)
            return null;

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

    /**
     * Finds an Map in a List of Maps who's property identified by <code>propertyName</code>
     * matches the <code>value</code> supplied. Returns null if not found.
     * @param objects The list of Maps to search.
     * @param propertyName The property name to examine in each Map.
     * @param value The value to compare against.
     * @return The Map where the value of propertyName equals value, or null if not found.
     */
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
