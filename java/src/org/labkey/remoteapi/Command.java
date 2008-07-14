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
 * Base class for all API commands. This class may also be used to
 * call APIs that do not yet have a specific class extension.
 */
public class Command
{
    public final static String CONTENT_TYPE_JSON = "application/json";

    private String _controllerName = null;
    private String _actionName = null;
    private Map<String,Object> _parameters = null;

    public Command(String controllerName, String actionName)
    {
        _controllerName = controllerName;
        _actionName = actionName;
    }

    public String getControllerName()
    {
        return _controllerName;
    }

    public String getActionName()
    {
        return _actionName;
    }

    public Map<String, Object> getParameters()
    {
        return _parameters;
    }

    public void setParameters(Map<String, Object> parameters)
    {
        _parameters = parameters;
    }

    public CommandResponse execute(Connection connection) throws IOException, EncoderException
    {
        return execute(connection, null);
    }

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
    
    protected CommandResponse createResponse(String text, int status)
    {
        return new CommandResponse(text, status);
    }

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

    protected HttpMethod createMethod()
    {
        return new GetMethod();
    }

    protected String getActionUrl(Connection connection, String folderPath) throws EncoderException
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

    @SuppressWarnings("UnusedDeclaration")
    protected String getParamValueAsString(Object param, String name)
    {
        return param.toString();
    }
}