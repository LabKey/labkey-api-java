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

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

/**
 * Created by adam on 4/15/2016.
 */

/**
 * A credentials provider that provides no credentials. Connections using
 * this provider will be granted guest access only.
 */
public class GuestCredentialsProvider implements CredentialsProvider
{
    @Override
    public void configureRequest(String baseUrl, HttpUriRequest request, HttpClientContext httpClientContext)
    {
        httpClientContext.setCredentialsProvider(null);
        request.removeHeaders("Authenticate");
    }
}
