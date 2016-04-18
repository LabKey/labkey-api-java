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
