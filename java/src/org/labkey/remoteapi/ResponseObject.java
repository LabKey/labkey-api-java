package org.labkey.remoteapi;

import java.util.Map;

/**
 * User: jeckels
 * Date: Apr 3, 2010
 */
public class ResponseObject
{
    protected final Map<String, Object> _allProperties;

    public ResponseObject(Map<String, Object> allProperties)
    {
        _allProperties = allProperties;
    }

    /** @return the full set of properties returned by the server */
    public Map<String, Object> getAllProperties()
    {
        return _allProperties;
    }
}
