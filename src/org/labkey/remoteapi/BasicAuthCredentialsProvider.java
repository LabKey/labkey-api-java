/*
 * Copyright (c) 2016 LabKey Corporation
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

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.labkey.remoteapi.security.EnsureLoginCommand;

import java.io.IOException;
import java.net.URI;

public class BasicAuthCredentialsProvider implements CredentialsProvider
{
    private final String _email;
    private final String _password;

    public BasicAuthCredentialsProvider(String email, String password)
    {
        _email = email;
        _password = password;
    }

    @Override
    public void configureClientBuilder(URI baseURI, HttpClientBuilder builder)
    {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope scope = new AuthScope(baseURI.getHost(), baseURI.getPort());
        Credentials credentials = new UsernamePasswordCredentials(_email, _password.toCharArray());
        provider.setCredentials(scope, credentials);
        builder.setDefaultCredentialsProvider(provider);

        // HttpClient doesn't provide a simple way to dictate connection-based Basic authentication, so jump through
        // some hoops: set a custom AuthSchemeRegistry that returns a customized version of BasicScheme.
        builder.setDefaultAuthSchemeRegistry(name -> !StandardAuthScheme.BASIC.equals(name) ? null : context -> new BasicScheme()
        {
            @Override
            public boolean isConnectionBased()
            {
                return true;
            }
        });
    }

    @Override
    public void configureRequest(URI baseURI, HttpUriRequest request, HttpClientContext httpClientContext)
    {
    }

    @Override
    public void initializeConnection(Connection connection) throws IOException, CommandException
    {
        new EnsureLoginCommand().execute(connection, "/home");
    }
}
