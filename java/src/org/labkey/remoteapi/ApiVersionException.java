package org.labkey.remoteapi;

import java.util.Map;

/**
 * User: adam
 * Date: Feb 23, 2009
 * Time: 1:13:26 PM
 */
public class ApiVersionException extends CommandException
{
    ApiVersionException(String message, int statusCode, Map<String,Object> properties)
    {
        super(message, statusCode, properties);
    }
}
