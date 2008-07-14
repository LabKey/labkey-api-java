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
 * Represents a LabKey Server installation.
 */
public class Connection
{
    private static MultiThreadedHttpConnectionManager _connectionManager = null;

    private String _baseUrl;
    private String _email;
    private String _password;
    private boolean _acceptSelfSignedCerts = true;

    public Connection(String baseUrl)
    {
        _baseUrl = baseUrl;
        setAcceptSelfSignedCerts(true);
    }

    public String getBaseUrl()
    {
        return _baseUrl;
    }

    public String getEmail()
    {
        return _email;
    }

    public void setEmail(String email)
    {
        _email = email;
    }

    public String getPassword()
    {
        return _password;
    }

    public void setPassword(String password)
    {
        _password = password;
    }

    public boolean isAcceptSelfSignedCerts()
    {
        return _acceptSelfSignedCerts;
    }

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

    public HttpClient getHttpClient() throws URIException
    {
        HttpClient client = new HttpClient(getConnectionManager());

        //if a user name was specified, set the credentials
        if(getEmail() != null)
        {
            client.getState().setCredentials(
                    new AuthScope(new URI(getBaseUrl(), false).getHost(),
                            AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                    new UsernamePasswordCredentials(getEmail(), getPassword())
            );
        }

        return client;
    }

    protected synchronized MultiThreadedHttpConnectionManager getConnectionManager()
    {
        if(null == _connectionManager)
            _connectionManager = new MultiThreadedHttpConnectionManager();
        return _connectionManager;
    }

}