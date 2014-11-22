/*
 * Copyright (c) 2008-2014 LabKey Corporation
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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;

import java.net.URI;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 3:17:18 PM
*/

/**
 * Base class for all commands that needs to post data to the server,
 * rather than providing parameters in the query string.
 * <p>
 * Client code will not typically use this class directly, but will
 * instead use one of the classes that extend this class (e.g.,
 * {@link org.labkey.remoteapi.query.UpdateRowsCommand}). 
 * <p>
 * However, if future versions of the LabKey Server expose new HTTP APIs requiring a POST
 * that are not yet supported with a specialized class in this library,
 * the developer may still invoke these APIs by creating an instance of the
 * PostCommand object directly, providing the controller and action name for
 * the new API. The post body may then be supplied by overriding the
 * {@link #getJsonObject()} method, returning the JSON object to post.
 *  
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class PostCommand<ResponseType extends CommandResponse> extends Command<ResponseType>
{
    private JSONObject _jsonObject = null;

    /**
     * Constructs a new PostCommand given a controller and action name.
     * @param controllerName The controller name.
     * @param actionName The action name.
     */
    public PostCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    public PostCommand(PostCommand source)
    {
        super(source);
        if (null != source.getJsonObject())
            _jsonObject = source.getJsonObject();
    }

    /**
     * Returns the JSON object to post, or null if the JSON object
     * has not yet been set. Override this method to provide the
     * JSON object dynamically.
     * @return The JSON object to post.
     */
    public JSONObject getJsonObject()
    {
        return _jsonObject;
    }

    /**
     * Sets the JSON object to post.
     * @param jsonObject The JSON object to post
     */
    public void setJsonObject(JSONObject jsonObject)
    {
        _jsonObject = jsonObject;
    }

    /**
     * Overrides {@link Command#createRequest(URI)} to create an
     * <code>HttpPost</code> object.
     * <p>
     * Override this method if your post command sends something other
     * than JSON in the post body. In your override, create the PostMethod
     * and set the RequestEntity appropriately.
     * @return The PostMethod object.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected HttpUriRequest createRequest(URI uri)
    {
        HttpPost request = new HttpPost(uri);

        //set the post body based on the supplied JSON object
        JSONObject json = getJsonObject();

        if (null != json)
        {
            if (!json.containsKey("apiVersion"))
                json.put("apiVersion", getRequiredVersion());

            request.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
        }

        return request;
    }

    @Override
    public PostCommand copy()
    {
        return new PostCommand(this);
    }
}
