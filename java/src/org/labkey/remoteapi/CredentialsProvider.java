package org.labkey.remoteapi;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.URISyntaxException;

/**
 * Created by adam on 4/15/2016.
 */
public interface CredentialsProvider
{
    void configureRequest(String baseUrl, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException, URISyntaxException;
}
