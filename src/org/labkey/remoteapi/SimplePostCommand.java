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
