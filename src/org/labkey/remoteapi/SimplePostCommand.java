/*
 * Copyright (c) 2023-2026 LabKey Corporation
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SimplePostCommand extends PostCommand<CommandResponse>
{
    private JSONObject _jsonObject = null;
    private Supplier<Map<String, Object>> _parameterMapFactory = HashMap::new;

    public SimplePostCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    /**
     * Returns the JSON object to post or null for no JSON.
     * @return The JSON object to post.
     */
    @Override
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
     * Returns a new, mutable parameter map initialized with the values from the map passed to
     * {@link #setParameters(Map)} method, if any.
     * @return The parameter map to use when building the URL.
     */
    @Override
    protected Map<String, Object> createParameterMap()
    {
        return _parameterMapFactory.get();
    }

    /**
     * Sets the URL parameter map.
     * @param parameters The values to use when initializing the parameter map
     */
    public void setParameters(Map<String, Object> parameters)
    {
        _parameterMapFactory = new Supplier<>()
        {
            private final Map<String, Object> _parameters = new HashMap<>(parameters);

            @Override
            public Map<String, Object> get()
            {
                return new HashMap<>(_parameters);
            }
        };
    }
}
