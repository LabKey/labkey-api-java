/*
 * Copyright (c) 2008-2014 LabKey Corporation
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
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

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
 * method, passing a populated parameter <code>Map&lt;String,Object&gt;</code>
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

    private String _controllerName = null;
    private String _actionName = null;
    private Map<String,Object> _parameters = null;
    private Integer _timeout = null;
    /** default timeout to 60 seconds */
    private static final int DEFAULT_TIMEOUT = 60000;
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
        if(null != source.getParameters())
            _parameters = new HashMap<String,Object>(source.getParameters());
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
        if(null == _parameters)
            _parameters = new HashMap<String,Object>();

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
     * Returns the current command timeout setting in milliseconds (defaults to 60000).
     * @return The current command timeout setting.
     */
    public int getTimeout()
    {
        return _timeout;
    }

    /**
     * Sets the current command timeout setting.
     * The value of the timeout parameter should be the maximum number of milliseconds
     * this command should wait for a response from the server. By default, this is set
     * to 60000 (60 seconds). To wait indefinitely, set this value to 0.
     * @param timeout The new timeout setting
     */
    public void setTimeout(int timeout)
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
     * If the server returns an error HTTP status code (>= 400), this method will throw
     * an instance of {@link CommandException}. Use its methods to determine the cause
     * of the error.
     * @param connection The connection on which this command should be executed.
     * @param folderPath The folder path in which to execute the command (e.g., "My Project/My Folder/My sub-folder").
     * You may also pass null to execute the command in the root container (usually requires site admin permission).
     * @return A CommandResponse (or derived class), which provides access to the returned text/data.
     * @throws CommandException Thrown if the server returned a non-success status code.
     * @throws org.apache.commons.httpclient.HttpException Thrown if the HttpClient library generated an exception.
     * @throws IOException Thrown if there was an IO problem.
     */
    public ResponseType execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        // Execute the method.  Throws CommandException for error responses.
        Response response = _execute(connection, folderPath);

        try
        {
            // For non-streaming Commands, read the entire response body into memory as JSON or a String.
            JSONObject json = null;
            String responseText = null;
            if (null != response.contentType && response.contentType.contains(Command.CONTENT_TYPE_JSON))
            {
                // Read entire response body and parse into JSON object
                json = (JSONObject)JSONValue.parse(new BufferedReader(new InputStreamReader(response.getInputStream())));
            }
            else
            {
                // Otherwise, read entire reponse body as text.
                responseText = response.getText();
            }

            return createResponse(responseText, response.statusCode, response.contentType, json);
        }
        finally
        {
            // Close the http connection
            response.close();
        }
    }

    /**
     * Response class allows clients to get an InputStream, consume lazily, and close the connection when complete.
     * NOTE: Internal experimental API -- exposes our internal usage of HttpMethod to clients which is bad.
     */
    protected static class Response implements Closeable
    {
        public final int statusCode;
        public final String contentType;
        private final HttpMethod method;

        Response(int statusCode, String contentType, HttpMethod method)
        {
            this.statusCode = statusCode;
            this.contentType = contentType;
            this.method = method;
        }

        public String getStatusText()
        {
            return method.getStatusText();
        }

        public InputStream getInputStream() throws IOException
        {
            return method.getResponseBodyAsStream();
        }

        public String getText() throws IOException
        {
            return method.getResponseBodyAsString();
        }

        // Caller is responsible for closing the response
        public void close()
        {
            method.releaseConnection();
        }

//        @Override
//        protected void finalize() throws Throwable
//        {
//            super.finalize();
//            close();
//        }
    }

    /** NOTE: Internal experimental API for handling streaming commands. */
    protected Response _execute(Connection connection, String folderPath) throws CommandException, IOException
    {
        assert null != getControllerName() : "You must set the controller name before executing the command!";
        assert null != getActionName() : "You must set the action name before executing the command!";

        //construct and initialize the HttpMethod
        final HttpMethod method = getHttpMethod(connection, folderPath);

        LogFactory.getLog(Command.class).info("Requesting URL: " + method.getURI().toString());

        //execute the method
        int status = connection.executeMethod(method);

        //get the content-type header
        Header contentTypeHeader = method.getResponseHeader("Content-Type");
        String contentType = (null == contentTypeHeader ? null : contentTypeHeader.getValue());

        Response response = new Response(status, contentType, method);

        //the HttpMethod should follow redirects automatically,
        //so the status code could be 2xx, 4xx, or 5xx
        if (status >= 400 && status < 600)
        {
            throwError(response);
        }

        return response;
    }

    private void throwError(Response r) throws IOException, CommandException
    {
        //use the status text as the message by default
        String message = null != r.getStatusText() ? r.getStatusText() : "(no status text)";

        // In commons-httpclient 3.1, getting the contents of the response will buffer
        // the entire response on the HttpMethod in memory. This seems ok for API error responses.
        String responseText = r.getText();
        JSONObject json = null;

        // If the content-type is json, try to parse the response text
        // and extract the "exception" property from the root-level object
        if (null != r.contentType && r.contentType.contains(CONTENT_TYPE_JSON))
        {
            // Parse JSON
            if (responseText != null && responseText.length() > 0)
            {
                json = (JSONObject) JSONValue.parse(responseText);
                if (json != null && json.containsKey("exception"))
                {
                    message = (String)json.get("exception");

                    if ("org.labkey.api.action.ApiVersionException".equals(json.get("exceptionClass")))
                        throw new ApiVersionException(message, r.statusCode, json, responseText);
                }
            }
        }

        throw new CommandException(message, r.statusCode, json, responseText);
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
     * Returns the appropriate, initialized HttpMethod implementation.
     * Extended classes may override this to change the way the HTTP method is initialized
     * <p>
     * Note that this method initializes the object created by the <code>createMethod()</code>
     * call. Extended classes should override that method to change which type of HttpMethod
     * gets created, and this one to change how it gets initialized.
     * @param connection The Connection against which the method will be executed.
     * @param folderPath The folder path in which the command will be executed.
     * This and the base URL from the connection will be used to construct the
     * first part of the URL.
     * @return The constructed HttpMethod instance.
     * @throws CommandException Thrown if there is a problem encoding the URL
     * @throws URIException Thrown if there is a problem parsing the base URL in the connection.
     */
    protected HttpMethod getHttpMethod(Connection connection, String folderPath) throws CommandException, URIException
    {
        HttpMethod method = createMethod();
        Integer timeout = _timeout;
        if (timeout == null)
        {
            timeout = connection.getTimeout();
        }
        method.getParams().setSoTimeout(timeout == null ? DEFAULT_TIMEOUT : timeout);
        method.setDoAuthentication(true);

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
        if(null != queryString)
            uri.setRawQuery(queryString.toCharArray());

        method.setURI(uri);
        return method;
    }

    /**
     * Creates the appropriate HttpMethod instance. Override to create something
     * other than <code>GetMethod</code>.
     * @return The HttpMethod instance.
     */
    protected HttpMethod createMethod()
    {
        return new GetMethod();
    }

    /**
     * Returns the portion of the URL before the query string for this command.
     * <p>
     * Note that the URL returned should <emph>not</emph> be encoded, as the calling function will encode it.
     * <p>
     * For example: "http://localhost:8080/labkey/MyProject/MyFolder/selectRows.api"
     * @param connection The connection to use.
     * @param folderPath The folder path to use.
     * @return The URL
     */
    protected URI getActionUrl(Connection connection, String folderPath) throws URIException
    {
        //start with the connection's base URL
        // Use a URI so that it correctly encodes each section of the overall URI
        URI uri = new URI(connection.getBaseUrl().replace('\\','/'), false);

        StringBuilder path = new StringBuilder(uri.getPath() == null ? "/" : uri.getPath());

        //add the controller name
        String controller = getControllerName();
        if(controller.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
            path.append('/');
        path.append(controller);

        //add the folderPath (if any)
        if(null != folderPath && folderPath.length() > 0)
        {
            String folderPathNormalized = folderPath.replace('\\', '/');
            if(folderPathNormalized.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
                path.append('/');
            path.append(folderPathNormalized);
        }

        //add the action name + ".api"
        String actionName = getActionName();
        if(actionName.charAt(0) != '/' && path.charAt(path.length() - 1) != '/')
            path.append('/');
        path.append(actionName);
        if(!actionName.endsWith(".api"))
            path.append(".api");

        uri.setPath(path.toString());
        return uri;
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
        Map<String,Object> params = getParameters();

        //add the required version if it's > 0
        if(getRequiredVersion() > 0)
            params.put(CommonParameters.apiVersion.name(), getRequiredVersion());

        StringBuilder qstring = new StringBuilder();
        URLCodec urlCodec = new URLCodec();
        try
        {
            for(String name : params.keySet())
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
            if(qstring.length() > 0)
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
