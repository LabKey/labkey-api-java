/*
 * Copyright (c) 2009-2016 LabKey Corporation
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
package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.ApiKeyCredentialsProvider;
import org.labkey.remoteapi.BasicAuthCredentialsProvider;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.NetrcCredentialsProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: adam
 * Date: Jan 10, 2009
 * Time: 5:20:32 PM
 */
public class SASConnection extends Connection
{
    @SuppressWarnings({"UnusedDeclaration"})
    // Called from SAS macros via reflective JavaObj interface
    public SASConnection(String baseUrl, String userName, String password) throws IOException, URISyntaxException
    {
        super(baseUrl, new BasicAuthCredentialsProvider(userName, password));
    }

    @SuppressWarnings({"UnusedDeclaration"})
    // Called from SAS macros via reflective JavaObj interface
    public SASConnection(String baseUrl) throws IOException, URISyntaxException
    {
        super(baseUrl, new NetrcCredentialsProvider(new URI(baseUrl)));
    }

    @SuppressWarnings({"UnusedDeclaration"})
    // Called from SAS macros via reflective JavaObj interface
    public SASConnection(String baseUrl, String apiKey) throws IOException, URISyntaxException
    {
        super(baseUrl, new ApiKeyCredentialsProvider(apiKey));
    }
}
