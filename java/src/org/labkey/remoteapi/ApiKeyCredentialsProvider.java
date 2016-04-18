package org.labkey.remoteapi;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.URISyntaxException;

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
    public void configureRequest(String baseUrl, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException, URISyntaxException
    {
        request.setHeader("apikey", _apiKey);
    }
}
