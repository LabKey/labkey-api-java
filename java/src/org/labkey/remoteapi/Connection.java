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

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * Represents connection information for a particular LabKey Server.
 * <p>
 * Create an instance of this class for each server you wish to interact with.
 * If the commands you execute reference data that requires a login, you must
 * also set an email address and password to be used for authentication.
 * Typically, developers will obtain these credentials from the program's environment,
 * such as via command-line parameters, environment variables, a properties file, etc.
 * <p>
 * After creating and initializing the Connection instance, pass it to the
 * <code>Command.execute()</code> method.
 * <p>Example:
 * <p>
 * <code>
 * <pre>
 *     Connection cn = new Connection("http://labkey.org");
 *     SelectRowsCommand cmd = new SelectRowsCommand("study", "Physical Exam");
 *     SelectRowsResponse response = cmd.execute(cn, "Home/Study/demo");
 * </pre>
 * </code>
 * <p>
 * Note that this class is not thread-safe. Do not share instances of Connection
 * between threads.
 * 
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class Connection
{
    private static MultiThreadedHttpConnectionManager _connectionManager = null;

    private String _baseUrl;
    private String _email;
    private String _password;
    private boolean _acceptSelfSignedCerts = true;
    private HttpClient _client = null;

    /**
     * Constructs a new Connection object given a base URL.
     * <p>
     * The baseUrl parameter should include the protocol, domain name, port,
     * and LabKey web application context path (if configured). For example
     * in a typical localhost configuration, the base URL would be:
     * <p>
     * <code>http://localhost:8080/labkey</code>
     * <p>
     * Note that https may also be used for the protocol. By default the
     * Connection is configured to accept self-signed SSL certificates, which
     * is fairly common for LabKey Server installations. If you do not want
     * to accept self-signed certificates, use
     * <code>setAcceptSelfSignedCerts(false)</code> to disable this behavior.
     * @param baseUrl The base URL
     */
    public Connection(String baseUrl)
    {
        _baseUrl = baseUrl;
        setAcceptSelfSignedCerts(true);
    }

    /**
     * Returns the base URL for this connection.
     * @return The base URL.
     */
    public String getBaseUrl()
    {
        return _baseUrl;
    }

    /**
     * Returns the email address to use for authentication.
     * @return The email address.
     */
    public String getEmail()
    {
        return _email;
    }

    /**
     * Sets the email address to use for authentication.
     * @param email The email address to use.
     */
    public void setEmail(String email)
    {
        _email = email;
    }

    /**
     * Returns the password to use for authentication.
     * @return The password.
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the password to use for authentication.
     * @param password The password to use.
     */
    public void setPassword(String password)
    {
        _password = password;
    }

    /**
     * Returns true if the connection should accept a self-signed
     * SSL certificate when using HTTPS, false otherwise. Defaults
     * to true.
     * @return true or false
     */
    public boolean isAcceptSelfSignedCerts()
    {
        return _acceptSelfSignedCerts;
    }

    /**
     * Sets the accept self-signed certificates option. Set to false
     * to disable automatic acceptance of self-signed SSL certificates
     * when using HTTPS.
     * @param acceptSelfSignedCerts set to false to not accept self-signed certificates
     */
    public void setAcceptSelfSignedCerts(boolean acceptSelfSignedCerts)
    {
        _acceptSelfSignedCerts = acceptSelfSignedCerts;

        //use the EasySSLProtocolSocketFactory
        //to accept self-signed certificates
        if(_acceptSelfSignedCerts)
            Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory)(new EasySSLProtocolSocketFactory()), 443));
        else
            Protocol.unregisterProtocol("https");
    }

    /**
     * Returns the HttpClient object to use for this connection.
     * @return The HttpClient object to use.
     * @throws URIException Thrown if the base URL could not be converted
     * into a legal URI.
     */
    public HttpClient getHttpClient() throws URIException
    {
        if(null == _client)
        {
            _client = new HttpClient(getConnectionManager());

            //if a user name was specified, set the credentials
            if(getEmail() != null)
            {
                _client.getState().setCredentials(
                        new AuthScope(new URI(getBaseUrl(), false).getHost(),
                                AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                        new UsernamePasswordCredentials(getEmail(), getPassword())
                );
                
                //send basic auth header on first request
                _client.getParams().setAuthenticationPreemptive(true);
            }
        }

        return _client;
    }

    /**
     * Returns the connection manager to use when initializing the
     * HttpClient object. By default, this method returns a
     * MultiThreadedHttpConnectionManager. Override to use a different
     * one.
     * @return The connection manager to use.
     */
    protected synchronized HttpConnectionManager getConnectionManager()
    {
        if(null == _connectionManager)
            _connectionManager = new MultiThreadedHttpConnectionManager();
        return _connectionManager;
    }

}