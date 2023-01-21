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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

import java.net.URI;

/**
 * Base class for all commands that need to post data to the server, rather than providing parameters
 * in the query string.
 * <p>
 * Client code will not use this class directly, but will instead use one of the classes that extend
 * this class (e.g., {@link org.labkey.remoteapi.query.UpdateRowsCommand}).
 * <p>
 * If a developer wishes to invoke actions that require POST and are not yet supported with a
 * specialized class in this library, the developer may invoke these APIs by creating an instance of
 * the {@link SimplePostCommand} class and setting the JSON object to post.
 */
public abstract class PostCommand<ResponseType extends CommandResponse> extends Command<ResponseType, HttpPost>
{
    /**
     * Constructs a new PostCommand given a controller and action name.
     * @param controllerName The controller name.
     * @param actionName The action name.
     */
    protected PostCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    /**
     * Returns the JSON object to post or null for no JSON. Override this method to provide parameters as JSON.
     * @return The JSON object to post.
     */
    public JSONObject getJsonObject()
    {
        return null;
    }

    /**
     * Creates the {@link HttpPost} instance used for the request. Override to modify the HttpPost object before use
     * or to send something other than JSON in the post body. In your override, create the HttpPost and set the
     * request entity appropriately.
     * @return The PostMethod object.
     */
    @Override
    protected HttpPost createRequest(URI uri)
    {
        HttpPost request = new HttpPost(uri);

        //set the post body based on the supplied JSON object
        JSONObject json = getJsonObject();

        if (null != json)
        {
            if (!json.has(CommonParameters.apiVersion.name()) && getRequiredVersion() > 0)
                json.put(CommonParameters.apiVersion.name(), getRequiredVersion());

            request.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
        }

        return request;
    }
}
