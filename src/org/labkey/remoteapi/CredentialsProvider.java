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

import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;

import java.io.IOException;
import java.net.URI;

public interface CredentialsProvider
{
    void configureClientBuilder(URI baseURI, HttpClientBuilder builder);
    void configureRequest(URI baseURI, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException;

    /**
     * Initialize the connection before its first request. If connection-based, authenticate the user. In all cases,
     * retrieve the CSRF token and session ID to use with subsequent requests on this connection.
     */
    void initializeConnection(Connection connection) throws IOException, CommandException;
}
