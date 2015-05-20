/*
 * Copyright (c) 2008-2014 LabKey Corporation
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

import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.labkey.remoteapi.security.EnsureLoginCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents connection information for a particular LabKey Server.
 * <p>
 * Create an instance of this class for each server you wish to interact with.
 * If the commands you execute reference data that requires a login, you must
 * also set an email address and password to be used for authentication.
 * Typically, developers will obtain these credentials from the program's environment,
 * such as via command-line parameters, environment variables, a properties file, etc.
 * <p>
 * After creating and initializing the Connection instance, pass it to the
 * <code>Command.execute()</code> method.
 * <p>Example:
 * <p>
 * <code>
 * <pre>
 *     Connection cn = new Connection("https://labkey.org");
 *     SelectRowsCommand cmd = new SelectRowsCommand("study", "Physical Exam");
 *     SelectRowsResponse response = cmd.execute(cn, "Home/Study/demo");
 *     for(Map&lt;String, Object&gt; row : response.getRows())
 *     {
 *         System.out.println(row.get("ParticipantId") + " weighs " + row.get("Weight"));
 *     }
 * </pre>
 * </code>
 * <p>
 * Example using Authentication
 * <p>
 * <code>
 * <pre>
 *     //get the user email and password from command-line arguments,
 *     //environment variables, a file, or some other mechanism.
 *     String user = getUser();
 *     String password = getPassword();
 *
 *     //create a new connection passing the user credentials
 *     Connection cn = new Connection("https://localhost:8080/labkey", user, password);
 *     SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");
 *     SelectRowsResponse response = cmd.execute(cn, "Api Test");
 * </pre>
 * </code>
 * <p>
 * Note that this class is not thread-safe. Do not share instances of Connection
 * between threads.
 * 
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class Connection
{
    private static final int DEFAULT_TIMEOUT = 60000;    // 60 seconds
    private static final HttpClientConnectionManager _connectionManager = new PoolingHttpClientConnectionManager();

    private String _baseUrl;
    private String _email;
    private String _password;
    private boolean _acceptSelfSignedCerts;
    private int _timeout = DEFAULT_TIMEOUT;
    private final HttpClientContext _httpClientContext;

    private final Map<Integer, CloseableHttpClient> _clientMap = new HashMap<>(3); // Typically very small

    /**
     * Constructs a new Connection object given a base URL.
     * <p>
     * This is equivalent to calling <code>Connection(baseUrl, null, null)</code>.
     * @param baseUrl The base URL
     * @see #Connection(String, String, String)
     */
    public Connection(String baseUrl)
    {
        this(baseUrl, null, null);
    }

    /**
     * Constructs a new Connection object given a base URL and user credentials.
     * <p>
     * The baseUrl parameter should include the protocol, domain name, port,
     * and LabKey web application context path (if configured). For example
     * in a typical localhost configuration, the base URL would be:
     * <p>
     * <code>http://localhost:8080/labkey</code>
     * <p>
     * Note that https may also be used for the protocol. By default the
     * Connection is configured to deny self-signed SSL certificates.
     * If you want to accept self-signed certificates, use
     * <code>setAcceptSelfSignedCerts(false)</code> to enable this behavior.
     * <p>
     * The email name and password should correspond to a valid user email
     * and password on the target server.
     * @param baseUrl The base URL
     * @param email The user email name to pass for authentication
     * @param password The user password to send for authentication
     */
    public Connection(String baseUrl, String email, String password)
    {
        _baseUrl = baseUrl;
        _email = email;
        _password = password;
        _httpClientContext = HttpClientContext.create();
        _httpClientContext.setCookieStore(new BasicCookieStore());
        setAcceptSelfSignedCerts(false);
    }

    /**
     * Returns the base URL for this connection.
     * @return The base URL.
     */
    public String getBaseUrl()
    {
        return _baseUrl;
    }

    /**
     * Returns the email address to use for authentication.
     * @return The email address.
     */
    public String getEmail()
    {
        return _email;
    }

    /**
     * Sets the email address to use for authentication.
     * @param email The email address to use.
     */
    public void setEmail(String email)
    {
        _email = email;
    }

    /**
     * Returns the password to use for authentication.
     * @return The password.
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the password to use for authentication.
     * @param password The password to use.
     */
    public void setPassword(String password)
    {
        _password = password;
    }

    /**
     * Returns true if the connection should accept a self-signed
     * SSL certificate when using HTTPS, false otherwise. Defaults
     * to true.
     * @return true or false
     */
    public boolean isAcceptSelfSignedCerts()
    {
        return _acceptSelfSignedCerts;
    }

    /**
     * Sets the accept self-signed certificates option. Set to false
     * to disable automatic acceptance of self-signed SSL certificates
     * when using HTTPS.
     * @param acceptSelfSignedCerts set to false to not accept self-signed certificates
     */
    public void setAcceptSelfSignedCerts(boolean acceptSelfSignedCerts)
    {
        // Handled in getHttpClient using 4.3.x approach documented here http://stackoverflow.com/questions/19517538/ignoring-ssl-certificate-in-apache-httpclient-4-3
        _acceptSelfSignedCerts = acceptSelfSignedCerts;
        _clientMap.clear();      // clear client cache
    }

    /**
     * Returns the CloseableHttpClient object to use for this connection.
     * @param timeout The socket timeout for this request
     * @return The CloseableHttpClient object to use.
     */
    public CloseableHttpClient getHttpClient(int timeout)
    {
        // We try to hand out the same HttpClient instance to share it across multiple requests, as the docs recommend.
        // However, HttpClient 4.x moved setting of the timeout to HttpClient construction time (it used to be on the
        // request-specific Method instance) but our API allows setting a timeout on a per-Command basis (which seems
        // perfectly reasonable). So, we stash and retrieve HttpClients using a Map<Integer, HttpClient>.

        CloseableHttpClient client = _clientMap.get(timeout);

        if (null == client)
        {
            HttpClientBuilder builder = HttpClientBuilder.create()
                    .setConnectionManager(_connectionManager)
                    .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(timeout).build())
                    .setDefaultCookieStore(_httpClientContext.getCookieStore());
            if (_acceptSelfSignedCerts)
            {
                try
                {
                    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                    sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
                    builder.setSSLSocketFactory(sslConnectionSocketFactory);
                }
                catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e)
                {
                    throw new RuntimeException(e);
                }
            }
            client = builder.build();

            _clientMap.put(timeout, client);
        }

        return client;
    }


    private String csrf = null;

    protected void beforeExecute(HttpRequest request)
    {
        if (null == csrf && request instanceof HttpPost)
        {
            // need to preemptively login
            // we're not really using the login form, just getting a JSESSIONID
            try
            {
                new Command("login", "login").execute(this, "/");
            }
            catch (Exception ignored)
            {
            }
        }
        if (null != csrf)
            request.setHeader("X-LABKEY-CSRF", csrf);
    }


    protected void afterExecute()
    {
        if (null == csrf)
        {
            for (Cookie c : _httpClientContext.getCookieStore().getCookies())
            {
                if ("JSESSIONID".equals(c.getName()))
                    csrf = c.getValue();
            }
        }
    }

    /** Ensures that the credentials have been used to authenticate the users and returns a client that can be used for other requests */
    public CloseableHttpClient ensureAuthenticated() throws IOException, AuthenticationException, CommandException
    {
        EnsureLoginCommand command = new EnsureLoginCommand();
        CommandResponse response = command.execute(this, "/home");
        return getHttpClient(getTimeout());
    }

    CloseableHttpResponse executeRequest(HttpUriRequest request, Integer timeout) throws IOException, URISyntaxException, AuthenticationException
    {
        //if a user name was specified, set the credentials
        if (getEmail() != null)
        {
            AuthScope scope = new AuthScope(new URI(getBaseUrl()).getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(getEmail(), getPassword());
            provider.setCredentials(scope, credentials);

            _httpClientContext.setCredentialsProvider(provider);
            request.addHeader(new BasicScheme().authenticate(credentials, request, _httpClientContext));
        }
        else
        {
            _httpClientContext.setCredentialsProvider(null);
            request.removeHeaders("Authenticate");
        }

        int clientTimeout = null == timeout ? getTimeout() : timeout;
        CloseableHttpClient client = getHttpClient(clientTimeout);
        beforeExecute(request);
        CloseableHttpResponse response = client.execute(request, _httpClientContext);
        afterExecute();

        return response;
    }

    /**
     * Set a default timeout for Commands that have not established their own timeouts. Null resets the Connection to the
     * default timeout (60 seconds). 0 means the request should never timeout.
     * @param timeout the length of the timeout waiting for the server response, in milliseconds
     */
    public void setTimeout(Integer timeout)
    {
        _timeout = timeout;
    }

    /**
     * The timeout used for Commands that have not established their own timeouts. 0 means the request should never timeout.
     * @return the length of the timeout waiting for the server response, in milliseconds
     */
    public int getTimeout()
    {
        return _timeout;
    }

    /**
     *
     * @param name The cookie name
     * @param value The cookie value
     * @param domain The domain to which the cookie is visible
     * @param path The path to which the cookie is visible
     * @param expiry The cookie's expiration date
     * @param isSecure Whether the cookie requires a secure connection
     */
    public void addCookie(String name, String value, String domain, String path, Date expiry, boolean isSecure)
    {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setExpiryDate(expiry);
        cookie.setSecure(isSecure);
        _httpClientContext.getCookieStore().addCookie(cookie);
    }
}