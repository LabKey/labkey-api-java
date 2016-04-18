package org.labkey.remoteapi;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by adam on 4/15/2016.
 */

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
     * @throws IOException
     */
    public NetrcCredentialsProvider(URI baseURI) throws IOException
    {
        this(baseURI.getHost());
    }

    /**
     * Attempt netrc lookup for the given host.
     *
     * @param host Just the host name, e.g., "www.labkey.org" or "localhost"
     * @throws IOException
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
    public void configureRequest(String baseUrl, HttpUriRequest request, HttpClientContext httpClientContext) throws AuthenticationException, URISyntaxException
    {
        _wrappedCredentialsProvider.configureRequest(baseUrl, request, httpClientContext);
    }
}
