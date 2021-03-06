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

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.URI;

/**
 * Created by adam on 4/15/2016.
 */
public class ApiKeyCredentialsProvider implements CredentialsProvider
{
    private final String _apiKey;

    public ApiKeyCredentialsProvider(String apiKey)
    {
        _apiKey = apiKey;
    }

    @Override
    public void configureRequest(URI baseURI, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException
    {
        request.setHeader("apikey", _apiKey);
    }
}
