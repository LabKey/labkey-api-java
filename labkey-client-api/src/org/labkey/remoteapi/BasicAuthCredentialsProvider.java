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
import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;

import java.net.URI;

/**
 * Created by adam on 4/15/2016.
 */
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
    public void configureRequest(URI baseURI, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException
    {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope scope = new AuthScope(baseURI.getHost(), -1);
        Credentials credentials = new UsernamePasswordCredentials(_email, _password.toCharArray());
        provider.setCredentials(scope, credentials);
        httpClientContext.setCredentialsProvider(provider);
    }
}
