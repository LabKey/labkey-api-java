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

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpStatus;
import org.labkey.remoteapi.security.EnsureLoginCommand;

import java.io.IOException;
import java.net.URI;

public class ApiKeyCredentialsProvider implements CredentialsProvider
{
    private final String _apiKey;

    public ApiKeyCredentialsProvider(String apiKey)
    {
        _apiKey = apiKey;
    }

    @Override
    public void configureClientBuilder(URI baseURI, HttpClientBuilder builder)
    {
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

    @Override
    public boolean shouldRetryRequest(CommandException exception, HttpUriRequest request)
    {
        // Determine if this is a Basic Auth challenge. If so, set the apikey header and retry the request. Subsequent
        // requests should use the session. This mimics the behavior of BasicAuthCredentialsProvider.

        String authHeaderValue = exception.getAuthHeaderValue();
        boolean authChallenge = (exception.getStatusCode() == HttpStatus.SC_UNAUTHORIZED && null != authHeaderValue && authHeaderValue.startsWith("Basic realm"));

        if (authChallenge)
            request.setHeader("apikey", _apiKey);

        return authChallenge;
    }
}
