package org.labkey.remoteapi;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by adam on 4/15/2016.
 */
public class BasicAuthCredentialsProvider implements CredentialsProvider
{
    private final String _email;
    private final String _password;

    public BasicAuthCredentialsProvider(String email, String password)
    {
        _email = email;
        _password = password;
    }

    @Override
    public void configureRequest(String baseUrl, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException, URISyntaxException
    {
        AuthScope scope = new AuthScope(new URI(baseUrl).getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        Credentials credentials = new UsernamePasswordCredentials(_email, _password);
        provider.setCredentials(scope, credentials);

        httpClientContext.setCredentialsProvider(provider);
        request.addHeader(new BasicScheme().authenticate(credentials, request, httpClientContext));
    }
}
