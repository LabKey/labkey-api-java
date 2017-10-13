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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Base class for all API commands. Typically, a developer will not
 * use this class directly, but will instead create one of the
 * classes that extend this class. For example, to select data,
 * create an instance of {@link org.labkey.remoteapi.query.SelectRowsCommand},
 * which provides helpful methods for setting options and obtaining
 * specific result types.
 * <p>
 * However, if future versions of the LabKey Server expose new HTTP APIs
 * that are not yet supported with a specialized class in this library,
 * the developer may still invoke these APIs by creating an instance of the
 * Command object directly, providing the controller and action name for
 * the new API. Parameters may then be specified by calling the <code>setParameters()</code>
 * method, passing a populated parameter <code>Map&lt;String, Object&gt;</code>
 * <p>
 * Note that this class is not thread-safe. Do not share instances of this class
 * or its descendants between threads, unless the descendant declares explicitly that
 * it is thread-safe.
 *
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class Command<ResponseType extends CommandResponse>
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
    private Map<String, Object> _parameters = null;
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
     * Constructs a new Command from an existing command
     * @param source A source Command
     */
    public Command(Command<ResponseType> source)
    {
        _actionName = source.getActionName();
        _controllerName = source.getControllerName();
        if (null != source.getParameters())
            _parameters = new HashMap<String, Object>(source.getParameters());
        _timeout = source._timeout;
        _requiredVersion = source.getRequiredVersion();
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
     * Returns the current parameter map, or null if a map has not yet been set.
     * Derived classes will typically override this method to provide a parameter
     * map based on data members set by specialized setter methods.
     * @return The current parameter map.
     */
    public Map<String, Object> getParameters()
    {
        if (null == _parameters)
            _parameters = new HashMap<String, Object>();

        return _parameters;
    }

    /**
     * Sets the current parameter map.
     * @param parameters The parameter map to use
     */
    public void setParameters(Map<String, Object> parameters)
    {
        _parameters = parameters;
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
                    json = (JSONObject) JSONValue.parse(new BufferedReader(new InputStreamReader(response.getInputStream())));
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
            return _httpResponse.getStatusLine().getReasonPhrase();
        }

        public int getStatusCode()
        {
            return _httpResponse.getStatusLine().getStatusCode();
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
            return _httpResponse.getEntity().getContent();
        }

        public String getText() throws IOException
        {
            if (_responseText != null)
                return _responseText;

            Scanner s = null;
            try
            {
                // Simple InputStream -> String conversion
                s = new Scanner(_httpResponse.getEntity().getContent()).useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
            }
            finally
            {
                if (s != null)
                    s.close();
            }
        }

        // Caller is responsible for closing the response
        public void close() throws IOException
        {
            _httpResponse.close();
        }
    }

    /* NOTE: Internal experimental API for handling streaming commands. */
    protected Response _execute(Connection connection, String folderPath) throws CommandException, IOException
    {
        assert null != getControllerName() : "You must set the controller name before executing the command!";
        assert null != getActionName() : "You must set the action name before executing the command!";
        CloseableHttpResponse httpResponse;

        final HttpUriRequest request;
        try
        {
            //construct and initialize the HttpUriRequest
            request = getHttpRequest(connection, folderPath);
            LogFactory.getLog(Command.class).info("Requesting URL: " + request.getURI().toString());

            //execute the request
            httpResponse = connection.executeRequest(request, getTimeout());
        }
        catch (URISyntaxException | AuthenticationException e)
        {
            throw new CommandException(e.getMessage());
        }

        //get the content-type header
        Header contentTypeHeader = httpResponse.getFirstHeader("Content-Type");
        String contentType = (null == contentTypeHeader ? null : contentTypeHeader.getValue());

        Header contentLengthHeader = httpResponse.getFirstHeader("Content-Length");
        Long contentLength = (null == contentLengthHeader ? null : Long.parseLong(contentLengthHeader.getValue()));

        Response response = new Response(httpResponse, contentType, contentLength);
        checkThrowError(response);
        return response;
    }

    protected void checkThrowError(Response response) throws IOException, CommandException
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

        // This buffers the entire response in memory, which seems OK for API error responses.
        String responseText = r.getText();
        JSONObject json = null;

        // If the content-type is json, try to parse the response text
        // and extract the "exception" property from the root-level object
        String contentType = r.getContentType();
        if (null != contentType && contentType.contains(CONTENT_TYPE_JSON))
        {
            // Parse JSON
            if (responseText != null && responseText.length() > 0)
            {
                json = (JSONObject) JSONValue.parse(responseText);
                if (json != null && json.containsKey("exception"))
                {
                    message = (String)json.get("exception");

                    if ("org.labkey.api.action.ApiVersionException".equals(json.get("exceptionClass")))
                        throw new ApiVersionException(message, r.getStatusCode(), json, responseText, contentType);

                    throw new CommandException(message, r.getStatusCode(), json, responseText, contentType);
                }
            }
        }

        if (throwByDefault)
            throw new CommandException(message, r.getStatusCode(), json, responseText, contentType);

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
        return (ResponseType)new CommandResponse(text, status, contentType, json, this.copy());
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
     * @throws CommandException Thrown if there is a problem encoding or parsing the URL
     * @throws URISyntaxException Thrown if there is a problem parsing the base URL in the connection.
     */
    protected HttpUriRequest getHttpRequest(Connection connection, String folderPath) throws CommandException, URISyntaxException
    {
// TODO        (Dave 11/11/14 -- as far as I can tell the default of the AuthSchemes is to automatically handle challenges
//        method.setDoAuthentication(true);

        //construct a URI from the results of the getActionUrl method
        //note that this method returns an unescaped URL, so pass
        //false for the second parameter to escape it
        URI uri = getActionUrl(connection, folderPath);

        //the query string values will need to be escaped as they are
        //put into the query string, and we don't want to re-escape the
        //entire thing, as the %, & and = characters will be escaped again
        //so add this separately as a raw value 
        //(indicating that it has already been escaped)
        String queryString = getQueryString();
        if (null != queryString)
            uri = new URIBuilder(uri).setQuery(queryString).build();

        return createRequest(uri);
    }

    /**
     * Creates the appropriate HttpUriRequest instance. Override to create something
     * other than <code>HttpGet</code>.
     * @param uri the uri to convert
     * @return The HttpUriRequest instance.
     */
    protected HttpUriRequest createRequest(URI uri)
    {
        return new HttpGet(uri);
    }

    /**
     * Returns the portion of the URL before the query string for this command.
     * <p>
     * Note that the URL returned should <em>not</em> be encoded, as the calling function will encode it.
     * <p>
     * For example: "http://localhost:8080/labkey/MyProject/MyFolder/selectRows.api"
     * @param connection The connection to use.
     * @param folderPath The folder path to use.
     * @return The URL
     * @throws URISyntaxException if the uri constructed from the parameters is malformed
     */
    protected URI getActionUrl(Connection connection, String folderPath) throws URISyntaxException
    {
        //start with the connection's base URL
        // Use a URI so that it correctly encodes each section of the overall URI
        URI uri = new URI(connection.getBaseUrl().replace('\\','/'));

        StringBuilder path = new StringBuilder(uri.getPath() == null || "".equals(uri.getPath()) ? "/" : uri.getPath());

        //add the controller name
        String controller = getControllerName();
        if (controller.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
            path.append('/');
        path.append(controller);

        //add the folderPath (if any)
        if (null != folderPath && folderPath.length() > 0)
        {
            String folderPathNormalized = folderPath.replace('\\', '/');
            if (folderPathNormalized.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
                path.append('/');
            path.append(folderPathNormalized);
        }

        //add the action name + ".api"
        String actionName = getActionName();
        if (actionName.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
            path.append('/');
        path.append(actionName);
        if (!actionName.endsWith(".api"))
            path.append(".api");

        return new URIBuilder(uri).setPath(path.toString()).build();
    }

    /**
     * Returns the query string portion of the URL for this command.
     * <p>
     * The parameters in the query string should be encoded to avoid parsing errors on the server.
     * @return The query string
     * @throws CommandException Thrown if there is a problem encoding the query string parameter names or values
     */
    protected String getQueryString() throws CommandException
    {
        Map<String, Object> params = getParameters();

        //add the required version if it's > 0
        if (getRequiredVersion() > 0)
            params.put(CommonParameters.apiVersion.name(), getRequiredVersion());

        StringBuilder qstring = new StringBuilder();
        URLCodec urlCodec = new URLCodec();
        try
        {
            for (String name : params.keySet())
            {
                Object value = params.get(name);
                if (value instanceof Collection)
                {
                    for (Object o : ((Collection) value))
                    {
                        appendParameter(qstring, urlCodec, name, o);
                    }
                }
                else
                {
                    appendParameter(qstring, urlCodec, name, value);
                }
            }
        }
        catch(EncoderException e)
        {
            throw new CommandException(e.getMessage());
        }

        return qstring.length() > 0 ? qstring.toString() : null;
    }

    private void appendParameter(StringBuilder qstring, URLCodec urlCodec, String name, Object value)
            throws EncoderException
    {
        String strValue = null == value ? null : getParamValueAsString(value, name);
        if(null != strValue)
        {
            if (qstring.length() > 0)
                qstring.append('&');
            qstring.append(urlCodec.encode(name));
            qstring.append('=');
            qstring.append(urlCodec.encode(strValue));
        }
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
    public double getRequiredVersion()
    {
        return _requiredVersion;
    }

    /**
     * Sets the required version number for this API call.
     * By default, the required version is 8.3, meaning that
     * this call requires LabKey Server version 8.3 or higher.
     * Set this to a higher number to required a later
     * version of the server.
     * @param requiredVersion The new required version
     */
    public void setRequiredVersion(double requiredVersion)
    {
        _requiredVersion = requiredVersion;
    }

    /**
     * Returns a copy of this object. Derived classes should override this
     * to copy their own data members   
     * @return A copy of this object
     */
    public Command copy()
    {
        return new Command(this);
    }
}
