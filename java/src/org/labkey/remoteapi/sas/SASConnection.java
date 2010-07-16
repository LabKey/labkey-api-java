/*
 * Copyright (c) 2009-2010 LabKey Corporation
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

import org.labkey.remoteapi.Connection;

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
        super(baseUrl);

        setEmail(userName);
        setPassword(password);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    // Called from SAS macros via reflective JavaObj interface
    public SASConnection(String baseUrl) throws IOException, URISyntaxException
    {
        super(baseUrl);

        URI uri = new URI(baseUrl);

        setCredentials(uri.getHost());
    }

    private void setCredentials(String host) throws IOException
    {
        NetrcFileParser parser = new NetrcFileParser();
        NetrcFileParser.NetrcEntry entry = parser.getEntry(host);

        if (null != entry)
        {
            setEmail(entry.getLogin());
            setPassword(entry.getPassword());
        }
    }
}
