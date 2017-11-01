/*
 * Copyright (c) 2008-2017 LabKey Corporation
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

import java.util.Map;

/**
 * Represents an exception that occurs while executing a command.
 * <p>
 * An instance of this class is typically thrown when the server returns
 * a non-success HTTP status code (&gt;= 400). The developer may catch
 * this exception type and use the <code>getStatusCode()</code> method
 * to retrieve the particular HTTP status code.
 * <p>
 * If the server generated an exception and sent details back to the
 * client, the exception message will be set as the message text, which
 * is returned from the <code>toString()</code> method. Other properties
 * about the exception, such as the exception class and stack trace, may
 * be obtained via the <code>getProperties()</code> method.
 */
public class CommandException extends Exception
{
    private final String _contentType;
    private final int _statusCode;
    private final Map<String, Object> _properties;
    private final String _responseText;

    /**
     * Constructs a new CommandException given a message only.
     * The status code and properties map will be set to 0 and null respectively
     * @param message The message text (should not be null).
     */
    public CommandException(String message)
    {
        this(message, 0, null, null, null);
    }

    /**
     * @deprecated Use {@link #CommandException(String, int, Map, String, String)}
     */
    public CommandException(String message, int statusCode, Map<String, Object> properties, String responseText)
    {
        this(message, statusCode, properties, responseText, null);
    }

    /**
     * Constructs a new CommandException given a message, HTTP status code,
     * exception property map, responseText, and contentType.
     * @param message The message text (should not be null).
     * @param statusCode The HTTP status code.
     * @param properties The exception property map (may be null)
     * @param responseText The full response text (may be null)
     * @param contentType The response content type (may be null)
     */
    public CommandException(String message, int statusCode, Map<String, Object> properties, String responseText, String contentType)
    {
        super(message);
        _statusCode = statusCode;
        _properties = properties;
        _responseText = responseText;
        _contentType = contentType;
    }

    public String getContentType()
    {
        return _contentType;
    }

    /**
     * Returns the HTTP status code returned by the server.
     * @return The HTTP status code.
     */
    public int getStatusCode()
    {
        return _statusCode;
    }

    /**
     * Returns the text of the response
     * @return The text of the response
     */
    public String getResponseText()
    {
        return _responseText;
    }

    /**
     * Returns the exception property map, or null if no map was set.
     * @return The exception property map or null.
     */
    public Map<String, Object> getProperties()
    {
        return _properties;
    }
}
