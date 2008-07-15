/*
 * Copyright (c) 2008 LabKey Corporation
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
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

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
 */
public class Command
{
    /**
     * A constant for the official JSON content type ("application/json")
     */
    public final static String CONTENT_TYPE_JSON = "application/json";

    private String _controllerName = null;
    private String _actionName = null;
    private Map<String,Object> _parameters = null;

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
     * Returns the current parameter map, or null if a map has not yet been set.
     * Derived classes will typically override this method to provide a parameter
     * map based on data members set by specialized setter methods.
     * @return The current parameter map.
     */
    public Map<String, Object> getParameters()
    {
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
     * Executes the command in the given folder on the specified connection, and returns
     * information about the response.
     * <p>
     * The <code>folderPath</code> parameter must be a valid folder path on the server
     * referenced by <code>connection</code>. To execute the command in the root container,
     * pass null for this parameter. Note however that executing commands in the root container
     * is typically useful only for administrator level operations.
     * <p>
     * The command will be executed against the server, using the credentials setup
     * in the <code>connection<code> object. If the server is invalid or the user does not
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
     * You may also pass null to execute the command in the root container (usually requires admin permission).
     * @return A CommandResponse (or derived class), which provides access to the returned text/data.
     * @throws CommandException Thrown if the server returned a non-success status code.
     * @throws org.apache.commons.httpclient.HttpException Thrown if the HttpClient library generated an exception.
     * @throws IOException Thrown if there was an IO problem.
     * @throws EncoderException Thrown if there was a problem encoding the URL
     */
    @SuppressWarnings("unchecked")
    public CommandResponse execute(Connection connection, String folderPath) throws IOException, EncoderException
    {
        assert null != getControllerName() : "You must set the controller name before executing the command!";
        assert null != getActionName() : "You must set the action name before executing the command!";

        //construct and initialize the HttpClient
        HttpClient client = connection.getHttpClient();

        //construct and initialize the HttpMethod
        HttpMethod method = getHttpMethod(connection, folderPath);

        int status = 0;
        String responseText = null;

        try
        {
            LogFactory.getLog(Command.class).info("Requesting URL: " + method.getURI().toString());
            //execute the method
            status = client.executeMethod(method);

            //get the response text
            responseText = method.getResponseBodyAsString();
        }
        finally
        {
            method.releaseConnection();
        }

        //the HttpMethod should follow redirects automatically,
        //so the status code could be 2xx, 4xx, or 5xx
        if(status >= 400 && status < 600)
        {
            //use the status text as the message by default
            String message = null != method.getStatusText() ? method.getStatusText() : "(no status text)";
            JSONObject json = null;

            //if the content-type is json, try to parse the response text
            //and extract the "exception" property from the root-level object
            Header contentType = method.getResponseHeader("Content-Type");
            if(null != contentType && null != contentType.getValue()
                    && contentType.getValue().contains(CONTENT_TYPE_JSON))
            {
                //our server always returns a JSON object as the root item
                json = (JSONObject)JSONValue.parse(responseText);
                if(null != json && json.containsKey("exception"))
                    message = (String)json.get("exception");
            }

            throw new CommandException(message, status, json);
        }

        return createResponse(responseText, status);
    }

    /**
     * Creates an instance of the response class, initialized with
     * the response text and the HTTP status code.
     * <p>
     * Override this method
     * to create an instance of a different class that extends CommandResponse
     * @param text The response text from the server.
     * @param status The HTTP status code
     * @return An instance of the response object.
     */
    protected CommandResponse createResponse(String text, int status)
    {
        return new CommandResponse(text, status);
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
     * @throws EncoderException Thrown if there is a problem encoding the URL
     * @throws URIException Thrown if there is a problem parsing the base URL in the connection.
     */
    protected HttpMethod getHttpMethod(Connection connection, String folderPath) throws EncoderException, URIException
    {
        HttpMethod method = createMethod();
        method.setDoAuthentication(true);

        //construct a URI from the results of the getActionUrl method
        //note that this method returns an unescaped URL, so pass
        //false for the second parameter to escape it
        URI uri = new URI(getActionUrl(connection, folderPath), false);

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
    protected String getActionUrl(Connection connection, String folderPath)
    {
        //start with the connection's base URL
        StringBuilder url = new StringBuilder(connection.getBaseUrl().replace('\\','/'));

        //add the controller name
        String controller = getControllerName();
        if(controller.charAt(0) != '/' && url.charAt(url.length() - 1) != '/')
            url.append('/');
        url.append(controller);

        //add the folderPath (if any)
        if(null != folderPath && folderPath.length() > 0)
        {
            String folderPathNormalized = folderPath.replace('\\', '/');
            if(folderPathNormalized.charAt(0) != '/' && url.charAt(url.length() - 1) != '/')
                url.append('/');
            url.append(folderPathNormalized);
        }

        //add the action name + ".api"
        String actionName = getActionName();
        if(actionName.charAt(0) != '/' && url.charAt(url.length() - 1) != '/')
            url.append('/');
        url.append(actionName);
        if(!actionName.endsWith(".api"))
            url.append(".api");

        return url.toString();
    }

    /**
     * Returns the query string portion of the URL for this command.
     * <p>
     * The parameters in the query string should be encoded to avoid parsing errors on the server.
     * @return The query string
     * @throws EncoderException Thrown if there is a problem encoding the query string parameter names or values
     */
    protected String getQueryString() throws EncoderException
    {
        Map<String,Object> params = getParameters();
        if(null == params)
            return null;

        StringBuilder qstring = new StringBuilder();
        URLCodec urlCodec = new URLCodec();
        for(String name : params.keySet())
        {
            Object value = params.get(name);
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

        return qstring.length() > 0 ? qstring.toString() : null;
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
}