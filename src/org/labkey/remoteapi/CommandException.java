/*
 * Copyright (c) 2008-2018 LabKey Corporation
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

import org.apache.hc.core5.http.impl.EnglishReasonPhraseCatalog;
import org.json.JSONObject;

import java.util.Locale;
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
    private final String _authHeaderValue;
    private final int _statusCode;
    private final JSONObject _jsonProperties;
    private final String _responseText;

    /**
     * Constructs a new CommandException given a message only.
     * The status code and properties map will be set to 0 and null respectively
     * @param message The message text (should not be null).
     */
    public CommandException(String message)
    {
        this(message, 0, null, null, null, null);
    }

    /**
     * Constructs a new CommandException given a message, HTTP status code,
     * exception property map, responseText, and contentType.
     * @param message The message text (should not be null).
     * @param statusCode The HTTP status code.
     * @param jsonProperties The exception property JSONObject (may be null)
     * @param responseText The full response text (may be null)
     * @param contentType The response content type (may be null)
     * @param authHeaderValue The value of the WWW-Authenticate header (may be null)
     */
    public CommandException(String message, int statusCode, JSONObject jsonProperties, String responseText, String contentType, String authHeaderValue)
    {
        super(buildMessage(message, statusCode));
        _statusCode = statusCode;
        _jsonProperties = jsonProperties;
        _responseText = responseText;
        _contentType = contentType;
        _authHeaderValue = authHeaderValue;
    }

    private static String buildMessage(String message, int statusCode)
    {
        if (statusCode == 0 || (message != null && !message.trim().isEmpty()))
        {
            return message;
        }

        // Use status code as message if none is specified
        StringBuilder sb = new StringBuilder();
        sb.append(statusCode);
        try
        {
            String reasonPhrase = EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.getDefault());
            sb.append(" : ");
            sb.append(reasonPhrase);
        }
        catch (IllegalArgumentException ignore) { /* Unknown status code */ }

        return sb.toString();
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
     * Returns the value of the WWW-Authenticate header
     * @return The value or null
     */
    public String getAuthHeaderValue()
    {
        return _authHeaderValue;
    }

    /**
     * Returns the exception property map, or null if no map was set.
     * @return The exception property map or null.
     */
    public Map<String, Object> getProperties()
    {
        return null == _jsonProperties ? null : _jsonProperties.toMap();
    }
}
