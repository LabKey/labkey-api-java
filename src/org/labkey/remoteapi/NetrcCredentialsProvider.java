/*
 * Copyright (c) 2016-2017 LabKey Corporation
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

/**
 * Attempts to find a .netrc or _netrc file and entry for the given host. If found, it will attempt basic auth using
 * the email and password in the entry. If file or entry are not found, it connects as guest.
 */
public class NetrcCredentialsProvider implements CredentialsProvider
{
    private final CredentialsProvider _wrappedCredentialsProvider;

    /**
     * Attempt netrc lookup for the URI's host.
     *
     * @param baseURI A valid base URI from which we'll extract the host name.
     * @throws IOException if there is an IO problem reading from the netrc file
     */
    public NetrcCredentialsProvider(URI baseURI) throws IOException
    {
        this(baseURI.getHost());
    }

    /**
     * Attempt netrc lookup for the given host.
     *
     * @param host Just the host name, e.g., "www.labkey.org" or "localhost"
     * @throws IOException if there is an IO problem reading from the netrc file
     */
    public NetrcCredentialsProvider(String host) throws IOException
    {
        NetrcFileParser parser = new NetrcFileParser();
        NetrcFileParser.NetrcEntry entry = parser.getEntry(host);

        if (null != entry)
            _wrappedCredentialsProvider = new BasicAuthCredentialsProvider(entry.getLogin(), entry.getPassword());
        else
            _wrappedCredentialsProvider = new GuestCredentialsProvider();
    }

    @Override
    public void configureClientBuilder(URI baseURI, HttpClientBuilder builder)
    {
        _wrappedCredentialsProvider.configureClientBuilder(baseURI, builder);
    }

    @Override
    public void configureRequest(URI baseURI, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException
    {
        _wrappedCredentialsProvider.configureRequest(baseURI, request, httpClientContext);
    }

    @Override
    public void initializeConnection(Connection connection) throws IOException, CommandException
    {
        _wrappedCredentialsProvider.initializeConnection(connection);
    }
}
