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

import org.apache.commons.logging.LogFactory;
import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.labkey.remoteapi.query.SelectRowsCommand;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Abstract base class for all API commands. Developers interact with concrete classes that
 * extend this class. For example, to select data, create an instance of
 * {@link SelectRowsCommand}, which provides helpful methods for
 * setting options and obtaining specific result types.
 * <p>
 * If a developer wishes to invoke actions that are not yet supported with a specialized class
 * in this library, the developer may invoke these APIs by creating an instance of the
 * {@link SimpleGetCommand} or {@link SimplePostCommand} classes, providing the controller and
 * action name for the API, and setting parameters or JSON to send.
 * <p>
 * Note that this class is not thread-safe. Do not share instances of this class or its
 * descendants between threads, unless the descendant declares explicitly that it is thread-safe.
 */
public abstract class Command<ResponseType extends CommandResponse, RequestType extends HttpUriRequest> implements HasRequiredVersion
{
    /**
     * A constant for the official JSON content type ("application/json")
     */
    public final static String CONTENT_TYPE_JSON = "application/json";

    /**
     * An enum of common parameter names used in API URLs.
     */
    public enum CommonParameters
    {
        apiVersion
    }

    private final String _controllerName;
    private final String _actionName;
    private Integer _timeout = null;
    private double _requiredVersion = 8.3;

    /**
     * Constructs a new Command object for calling the specified
     * API action on the specified controller.
     * @param controllerName The name of the controller from which the action is exposed
     * @param actionName The name of the action to call
     */
    public Command(String controllerName, String actionName)
    {
        assert null != controllerName;
        assert null != actionName;

        _controllerName = controllerName;
        _actionName = actionName;
    }

    /**
     * Returns the controller name specified when constructing this object.
     * @return The controller name.
     */
    public String getControllerName()
    {
        return _controllerName;
    }

    /**
     * Returns the action name specified when constructing this object.
     * @return The action name.
     */
    public String getActionName()
    {
        return _actionName;
    }

    /**
     * Makes an immutable copy of the parameter map used for building the URL available to callers. Typically used for
     * logging and testing.
     * @return An immutable map of parameters used when building the URL.
     */
    public final Map<String, Object> getParameters()
    {
        return Collections.unmodifiableMap(createParameterMap());
    }

    /**
     * Returns a new, mutable parameter map. Derived classes will typically override this
     * method to put values passed to specialized setter methods into the map.
     * @return The parameter map to use when building the URL.
     */
    protected Map<String, Object> createParameterMap()
    {
        return new HashMap<>();
    }

    /**
     * Returns the current timeout setting in milliseconds for this Command.
     * @return The current command timeout setting. Null means defer to Connection timeout. 0 means no timeout.
     */
    public Integer getTimeout()
    {
        return _timeout;
    }

    /**
     * Sets the timeout for this Command.
     * The value of the timeout parameter should be the maximum number of milliseconds
     * this command should wait for a response from the server.
     * @param timeout The new timeout setting, with null meaning defer to the Connection and 0 meaning wait indefinitely.
     */
    public void setTimeout(Integer timeout)
    {
        _timeout = timeout;
    }

    /**
     * Executes the command in the given folder on the specified connection, and returns
     * information about the response.
     * <p>
     * The <code>folderPath</code> parameter must be a valid folder path on the server
     * referenced by <code>connection</code>. To execute the command in the root container,
     * pass null for this parameter. Note however that executing commands in the root container
     * is typically useful only for administrator level operations.
     * <p>
     * The command will be executed against the server, using the credentials setup
     * in the <code>connection</code> object. If the server is invalid or the user does not
     * have sufficient permissions, a <code>CommandException</code> will be thrown with
     * the 403 (Forbidden) HTTP status code.
     * <p>
     * Note that the command is executed synchronously, so the calling code will block until the
     * entire response has been read from the server. To execute a command asynchronously, use
     * a separate thread.
     * <p>
     * If the server returns an error HTTP status code (&gt;= 400), this method will throw
     * an instance of {@link CommandException}. Use its methods to determine the cause
     * of the error.
     * @param connection The connection on which this command should be executed.
     * @param folderPath The folder path in which to execute the command (e.g., "My Project/My Folder/My sub-folder").
     * You may also pass null to execute the command in the root container (usually requires site admin permission).
     * @return A CommandResponse (or derived class), which provides access to the returned text/data.
     * @throws CommandException Thrown if the server returned a non-success status code.
     * @throws IOException Thrown if there was an IO problem.
     */
    public ResponseType execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        // Execute the command. Throws CommandException for error responses.
        try (Response response = _execute(connection, folderPath))
        {
            // For non-streaming Commands, read the entire response body into memory as JSON or a String.
            // The json and responseText will already be parsed when checking for an exception message on small 200 responses.
            JSONObject json = response._json;
            String responseText = response._responseText;
            String contentType = response.getContentType();

            if (json == null)
            {
                if (null != contentType && contentType.contains(Command.CONTENT_TYPE_JSON))
                {
                    // Read entire response body and parse into JSON object
                    try (Reader reader = response.getReader())
                    {
                        json = new JSONObject(new JSONTokener(reader));
                    }
                }
                else
                {
                    // Otherwise, read entire response body as text.
                    responseText = response.getText();
                }
            }

            return createResponse(responseText, response.getStatusCode(), contentType, json);
        }
    }

    /**
     * Response class allows clients to get an InputStream, consume lazily, and close the connection when complete.
     */
    protected static class Response implements Closeable
    {
        private final CloseableHttpResponse _httpResponse;
        private final String _contentType;
        private final Long _contentLength;

        // The json and responseText will already be parsed when checking for an exception message on small 200 responses.
        // The parsed json should not be available to the client library user since exposing the fields would make streaming responses impossible.
        private String _responseText;
        private JSONObject _json;

        private Response(CloseableHttpResponse httpResponse, String contentType, Long contentLength)
        {
            _httpResponse = httpResponse;
            _contentType = contentType;
            _contentLength = contentLength;
        }

        public String getStatusText()
        {
            return _httpResponse.getReasonPhrase();
        }

        public int getStatusCode()
        {
            return _httpResponse.getCode();
        }

        public String getContentType()
        {
            return _contentType;
        }

        public Long getContentLength()
        {
            return _contentLength;
        }

        public InputStream getInputStream() throws IOException
        {
            if (_responseText != null)
            {
                // We've already consumed the input stream and stashed the result, so use that instead of going back
                // to the stream
                return new ByteArrayInputStream(_responseText.getBytes(StandardCharsets.UTF_8));
            }
            return _httpResponse.getEntity().getContent();
        }

        public Reader getReader() throws IOException
        {
            if (_responseText != null)
            {
                // We've already consumed the input stream and stashed the result, so use that instead of going back
                // to the stream
                return new StringReader(_responseText);
            }
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        public String getText() throws IOException
        {
            if (_responseText != null)
                return _responseText;

            try (Scanner s = new Scanner(_httpResponse.getEntity().getContent(), StandardCharsets.UTF_8).useDelimiter("\\A"))
            {
                // Simple InputStream -> String conversion
                return s.hasNext() ? s.next() : "";
            }
        }

        public String getHeaderValue(String name)
        {
            Header header = null;
            try
            {
                header = _httpResponse.getHeader(name);
            }
            catch (ProtocolException ignored)
            {
            }
            return null == header ? null : header.getValue();
        }

        // Caller is responsible for closing the response
        @Override
        public void close() throws IOException
        {
            _httpResponse.close();
        }
    }

    protected Response _execute(Connection connection, String folderPath) throws CommandException, IOException
    {
        assert null != getControllerName() : "You must set the controller name before executing the command!";
        assert null != getActionName() : "You must set the action name before executing the command!";

        try
        {
            //construct and initialize the HttpUriRequest
            final HttpUriRequest request = getHttpRequest(connection, folderPath);
            try
            {
                return executeRequest(connection, request);
            }
            catch (CommandException e)
            {
                if (connection.getCredentialsProvider().shouldRetryRequest(e, request))
                    return executeRequest(connection, request);
                else
                    throw e;
            }
        }
        catch (URISyntaxException | AuthenticationException e)
        {
            throw new CommandException(e.getMessage());
        }
    }

    private Response executeRequest(Connection connection, HttpUriRequest request) throws AuthenticationException, IOException, CommandException
    {
        LogFactory.getLog(Command.class).info("Requesting URL: " + request.getRequestUri());

        //execute the request
        CloseableHttpResponse httpResponse = connection.executeRequest(request, getTimeout());

        //get the content-type header
        Header contentTypeHeader = httpResponse.getFirstHeader("Content-Type");
        String contentType = (null == contentTypeHeader ? null : contentTypeHeader.getValue());

        Header contentLengthHeader = httpResponse.getFirstHeader("Content-Length");
        Long contentLength = (null == contentLengthHeader ? null : Long.parseLong(contentLengthHeader.getValue()));

        Response response = new Response(httpResponse, contentType, contentLength);
        checkThrowError(response);

        return response;
    }

    private void checkThrowError(Response response) throws IOException, CommandException
    {
        int status = response.getStatusCode();

        // Always throw an exception if status is 4xx or 5xx.
        //the HttpUriRequest should follow redirects automatically,
        //so the status code could be 2xx, 4xx, or 5xx.
        if (status >= 400 && status < 600)
        {
            throwError(response, true);
        }

        // Check for a 200 status but with an exception in the json response body.
        // To support streaming large responses, only check for an exception if the response size is less than 4K
        if (status == 200 &&
                response.getContentType() != null && response.getContentType().contains(CONTENT_TYPE_JSON) &&
                response.getContentLength() != null && response.getContentLength() < 4096)
        {
            throwError(response, false);
        }
    }

    private void throwError(Response r, boolean throwByDefault) throws IOException, CommandException
    {
        //use the status text as the message by default
        String message = null != r.getStatusText() ? r.getStatusText() : "(no status text)";

        String authHeaderValue = r.getHeaderValue(HttpHeaders.WWW_AUTHENTICATE);

        // This buffers the entire response in memory, which seems OK for API error responses.
        String responseText = r.getText();
        JSONObject json = null;

        // If the content-type is json, try to parse the response text
        // and extract the "exception" property from the root-level object
        String contentType = r.getContentType();
        if (null != contentType && contentType.contains(CONTENT_TYPE_JSON))
        {
            // Parse JSON
            if (responseText != null && !responseText.isEmpty())
            {
                json = new JSONObject(responseText);
                if (json.has("exception"))
                {
                    message = json.getString("exception");

                    if ("org.labkey.api.action.ApiVersionException".equals(json.opt("exceptionClass")))
                        throw new ApiVersionException(message, r.getStatusCode(), json, responseText, contentType, authHeaderValue);

                    throw new CommandException(message, r.getStatusCode(), json, responseText, contentType, authHeaderValue);
                }
            }
        }

        if (throwByDefault)
            throw new CommandException(message, r.getStatusCode(), json, responseText, contentType, authHeaderValue);

        // If we didn't encounter an exception property on the json object, save the fully consumed text and parsed json on the Response object
        r._json = json;
        r._responseText = responseText;
    }

    /**
     * Creates an instance of the response class, initialized with
     * the response text, the HTTP status code, and parsed JSONObject.
     * <p>
     * Override this method
     * to create an instance of a different class that extends CommandResponse
     * @param text The response text from the server.
     * @param status The HTTP status code.
     * @param contentType The Content-Type header value.
     * @param json The parsed JSONObject (or null if no JSON was returned).
     * @return An instance of the response object.
     */
    protected ResponseType createResponse(String text, int status, String contentType, JSONObject json)
    {
        return (ResponseType)new CommandResponse(text, status, contentType, json);
    }

    /**
     * Returns the appropriate, initialized HttpUriRequest implementation.
     * Extended classes may override this to change the way the HTTP method is initialized
     * <p>
     * Note that this method initializes the object created by the <code>createRequest()</code>
     * call. Extended classes should override that method to change which type of HttpUriRequest
     * gets created, and this one to change how it gets initialized.
     * @param connection The Connection against which the request will be executed.
     * @param folderPath The folder path in which the command will be executed.
     * This and the base URL from the connection will be used to construct the
     * first part of the URL.
     * @return The constructed HttpUriRequest instance.
     * @throws URISyntaxException Thrown if there is a problem parsing the base URL in the connection.
     */

    protected RequestType getHttpRequest(Connection connection, String folderPath) throws URISyntaxException
    {
        //construct a URI from connection base URI, folder path, and current parameters
        URI uri = createURI(connection, folderPath);

        return createRequest(uri);
    }

    /**
     * Creates the appropriate HttpUriRequest instance. Override to create <code>HttpGet</code>, <code>HttpPost</code>, etc.
     * @param uri the uri to convert
     * @return The HttpUriRequest instance.
     */
    protected abstract RequestType createRequest(URI uri);

    /**
     * Returns a full URI for this Command, including base URI, folder path, and query string.
     *
     * @param connection The connection to use.
     * @param folderPath The folder path to use.
     * @return The URI
     * @throws URISyntaxException if the uri constructed from the parameters is malformed
     */
    private URI createURI(Connection connection, String folderPath) throws URISyntaxException
    {
        URI uri = connection.getBaseURI();

        StringBuilder path = new StringBuilder(uri.getPath() == null || "".equals(uri.getPath()) ? "/" : uri.getPath());

        //add the folderPath (if any)
        if (null != folderPath && !folderPath.isEmpty())
        {
            String folderPathNormalized = folderPath.replace('\\', '/');
            if (folderPathNormalized.charAt(0) == '/') // strip leading slash
                folderPathNormalized = folderPathNormalized.substring(1);
            if (path.charAt(path.length() - 1) != '/')
                path.append('/');
            path.append(folderPathNormalized);
        }

        if (path.charAt(path.length() - 1) != '/')
            path.append('/');

        //add the controller name
        String controller = getControllerName();
        if (controller.contains("/"))
            throw new IllegalArgumentException("Controller name should not contain a slash (/)");
        path.append(controller);

        //add the action name + ".api"
        String actionName = getActionName();
        if (actionName.contains("/"))
            throw new IllegalArgumentException("Action name should not contain a slash (/)");
        path.append("-").append(actionName);
        if (!actionName.endsWith(".api"))
            path.append(".api");

        URIBuilder builder = new URIBuilder(uri).setPath(path.toString());

        Map<String, Object> params = createParameterMap();

        //add the required version if it's > 0
        if (getRequiredVersion() > 0)
            params.put(CommonParameters.apiVersion.name(), getRequiredVersion());

        if (!params.isEmpty())
        {
            for (String name : params.keySet())
            {
                Object value = params.get(name);
                if (value instanceof Collection<?> col)
                {
                    for (Object o : col)
                    {
                        addParameter(builder, name, o);
                    }
                }
                else
                {
                    addParameter(builder, name, value);
                }
            }
        }

        return builder.build();
    }

    private void addParameter(URIBuilder builder, String name, Object value)
    {
        String strValue = null == value ? null : getParamValueAsString(value, name);
        if (null != strValue)
            builder.addParameter(name, strValue);
    }

    /**
     * Returns an appropriate string encoding of the parameter value.
     * <p>
     * Extended classes may wish to override this method to do special string encoding
     * of particular parameter data types. This class will simply return the results of
     * <code>param.toString()</code>.
     * @param param The parameter value
     * @param name The parameter name
     * @return The string form of the parameter value.
     */
    @SuppressWarnings("UnusedDeclaration")
    protected String getParamValueAsString(Object param, String name)
    {
        return param.toString();
    }

    /**
     * Returns the required version number of this API call.
     * @return The required version number
     */
    @Override
    public double getRequiredVersion()
    {
        return _requiredVersion;
    }

    /**
     * Sets the required version number for this API call.
     * By default, the required version is 8.3, meaning that
     * this call requires LabKey Server version 8.3 or higher.
     * Set this to a higher number to require a later
     * version of the server.
     * @param requiredVersion The new required version
     */
    public void setRequiredVersion(double requiredVersion)
    {
        _requiredVersion = requiredVersion;
    }
}
