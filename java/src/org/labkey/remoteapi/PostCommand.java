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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 3:17:18 PM
*/

/**
 * Used as the base class for all commands that need to post data
 */
public class PostCommand extends Command
{
    private JSONObject _jsonObject = null;

    public PostCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    public JSONObject getJsonObject()
    {
        return _jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject)
    {
        _jsonObject = jsonObject;
    }

    protected HttpMethod createMethod()
    {
        PostMethod method = new PostMethod();

        //set the post body based on the supplied JSON object
        if(null != getJsonObject())
        {
            RequestEntity entity = null;
            try
            {
                method.setRequestEntity(new StringRequestEntity(getJsonObject().toString(),
                        Command.CONTENT_TYPE_JSON, "UTF-8"));
            }
            catch(UnsupportedEncodingException e)
            {
                //just ignore for now--UTF-8 should always be supported
            }
        }

        return method;
    }
}