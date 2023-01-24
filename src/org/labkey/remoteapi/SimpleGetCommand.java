package org.labkey.remoteapi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleGetCommand extends GetCommand<CommandResponse>
{
    private Supplier<Map<String, Object>> _parameterMapFactory = HashMap::new;

    public SimpleGetCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
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
