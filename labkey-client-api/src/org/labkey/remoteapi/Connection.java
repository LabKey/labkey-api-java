/*
 * Copyright (c) 2008-2018 LabKey Corporation
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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContextBuilder;
import org.labkey.remoteapi.security.EnsureLoginCommand;
import org.labkey.remoteapi.security.ImpersonateUserCommand;
import org.labkey.remoteapi.security.LogoutCommand;
import org.labkey.remoteapi.security.StopImpersonatingCommand;
import org.labkey.remoteapi.security.WhoAmICommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

/**
 * Represents connection information for a particular LabKey Server.
 * <p>
 * Create an instance of this class for each server you wish to interact with.
 * If the commands you execute require a login, you must also configure
 * authentication via one of the supported methods: retrieving email address
 * and password from a .netrc/_netrc file, providing an api key, or providing
 * email address and password directly (which could be obtained from the
 * program's environment, such as via command-line parameters, environment
 * variables, a properties file, etc. See the individual constructors and
 * implementations of <code>CredentialsProvider</code> for more details.
 * </p>
 * After creating and initializing the Connection instance, pass it to the
 * <code>Command.execute()</code> method.
 * <p>Example:
 * </p>
 * <pre><code>
 *     Connection cn = new Connection("https://www.labkey.org");
 *     SelectRowsCommand cmd = new SelectRowsCommand("study", "Physical Exam");
 *     SelectRowsResponse response = cmd.execute(cn, "Home/Study/demo");
 *     for(Map&lt;String, Object&gt; row : response.getRows())
 *     {
 *         System.out.println(row.get("ParticipantId") + " weighs " + row.get("Weight"));
 *     }
 * </code></pre>
 * <p>
 * Example using Authentication
 * </p>
 * <pre>
 * <code>
 *     //get the user email and password from command-line arguments,
 *     //environment variables, a file, or some other mechanism.
 *     String user = getUser();
 *     String password = getPassword();
 *
 *     //create a new connection passing the user credentials
 *     Connection cn = new Connection("https://localhost:8080/labkey", user, password);
 *     SelectRowsCommand cmd = new SelectRowsCommand("lists", "People");
 *     SelectRowsResponse response = cmd.execute(cn, "Api Test");
 * </code>
 * </pre>
 * <p>
 * Note that this class is not thread-safe. Do not share instances of Connection
 * between threads.
 * </p>
 * 
 * @author Dave Stearns, LabKey Corporation
 * @version 1.0
 */
public class Connection
{
    public static final String X_LABKEY_CSRF = "X-LABKEY-CSRF";
    public static final String JSESSIONID = "JSESSIONID";

    private static final int DEFAULT_TIMEOUT = 60000;    // 60 seconds
    private static final HttpClientConnectionManager _connectionManager = new PoolingHttpClientConnectionManager();

    private final URI _baseURI;
    private final CredentialsProvider _credentialsProvider;
    private final HttpClientContext _httpClientContext;

    private CloseableHttpClient _client;
    private boolean _acceptSelfSignedCerts;
    private int _timeout = DEFAULT_TIMEOUT;
    private String _proxyHost;
    private Integer _proxyPort;
    private String _csrf;
    private String _sessionId;

    // The user email when impersonating a user
    private String _impersonateUser;
    private String _impersonatePath;

    /**
     * Constructs a new Connection object given a base URL and a credentials provider.
     * <p>
     * The baseURI parameter should include the protocol, domain name, port,
     * and LabKey web application context path (if configured). For example
     * in a typical localhost configuration, the base URL would be:
     * </p>
     * <code>new URI("http://localhost:8080/labkey")</code>
     * <p>
     * Note that https may also be used for the protocol. By default the
     * Connection is configured to deny self-signed SSL certificates.
     * If you want to accept self-signed certificates, use
     * <code>setAcceptSelfSignedCerts(false)</code> to enable this behavior.
     * </p>
     * @param baseURI Base URI for this Connection
     * @param credentialsProvider A credentials provider
     */
    public Connection(URI baseURI, CredentialsProvider credentialsProvider)
    {
        if (baseURI.getHost() == null || baseURI.getScheme() == null)
        {
            throw new IllegalArgumentException("Invalid server URL: " + baseURI.toString());
        }
        _baseURI = baseURI;
        _credentialsProvider = credentialsProvider;
        _httpClientContext = HttpClientContext.create();
        _httpClientContext.setCookieStore(new BasicCookieStore());
        setAcceptSelfSignedCerts(false);
    }

    /**
     * Constructs a new Connection object given a base URL and a credentials provider.
     * <p>
     * The baseUrl parameter should include the protocol, domain name, port,
     * and LabKey web application context path (if configured). For example
     * in a typical localhost configuration, the base URL would be:
     * </p>
     * <code>http://localhost:8080/labkey</code>
     * <p>
     * @param baseUrl The base URL
     * @param credentialsProvider A credentials provider
     * @see #Connection(URI, CredentialsProvider)
     */
    public Connection(String baseUrl, CredentialsProvider credentialsProvider)
    {
        this(toURI(baseUrl), credentialsProvider);
    }

    /**
     * Constructs a new Connection object with a base URL that attempts authentication via .netrc/_netrc entry, if present.
     * If not present, connects as guest.
     * @param baseUrl The base URL
     * @throws IOException if there are problems reading the credentials
     * @see NetrcCredentialsProvider
     * @see #Connection(URI, CredentialsProvider)
     */
    public Connection(String baseUrl) throws IOException
    {
        this(toURI(baseUrl), new NetrcCredentialsProvider(toURI(baseUrl)));
    }

    /**
     * Constructs a new Connection object for a base URL that attempts basic authentication.
     * <p>
     * This is equivalent to calling <code>Connection(baseUrl, new BasicAuthCredentialsProvider(email, password))</code>.
     * The email and password should correspond to a valid user email
     * and password on the target server.
     * @param baseUrl The base URL
     * @param email The user email address to pass for authentication
     * @param password The user password to send for authentication
     * @see BasicAuthCredentialsProvider#BasicAuthCredentialsProvider(String, String)
     * @see #Connection(URI, CredentialsProvider)
     */
    public Connection(String baseUrl, String email, String password)
    {
        this(toURI(baseUrl), new BasicAuthCredentialsProvider(email, password));
    }

    /**
     * Returns the base URL for this connection.
     * @return The base URL.
     * @deprecated Use {@link #getBaseURI()}
     */
    @Deprecated
    public String getBaseUrl()
    {
        return _baseURI.toString();
    }

    /**
     * Returns the base URI for this connection.
     * @return The target LabKey instance's base URI
     */
    public URI getBaseURI()
    {
        return _baseURI;
    }

    /**
     * Returns the CloseableHttpClient object to use for this connection.
     * @return The CloseableHttpClient object to use.
     */
    public CloseableHttpClient getHttpClient()
    {
        if (null == _client)
        {
            _client = clientBuilder().build();
        }

        return _client;
    }

    /**
     * Create the HttpClientBuilder based on this Connection's configuration options.
     * @return The builder for an HttpClient
     */
    protected HttpClientBuilder clientBuilder()
    {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setConnectionManager(_connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(getTimeout()).build())
                .setDefaultCookieStore(_httpClientContext.getCookieStore());

        if (_proxyHost != null && _proxyPort != null)
            builder.setProxy(new HttpHost(_proxyHost, _proxyPort));

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

        return builder;
    }

    /**
     * If necessary, prime the Connection for non-GET requests.
     * Executes a dummy command to trigger authentication and populate CSRF and session info.
     * @param request HttpRequest that is about to be executed
     */
    protected void beforeExecute(HttpRequest request)
    {
        if (null == _csrf &&
                request instanceof HttpRequestBase && !"GET".equals(((HttpRequestBase) request).getMethod()))
        {
            // make a request to get a JSESSIONID
            try
            {
                new WhoAmICommand().execute(this, "/");
            }
            catch (Exception ignored)
            {
            }
        }
        if (null != _csrf)
            request.setHeader(X_LABKEY_CSRF, _csrf);
        if (null != _sessionId)
            request.setHeader(JSESSIONID, _sessionId);
    }


    protected void afterExecute()
    {
        // Always update our CSRF token as the session may be new since our last request
        for (Cookie c : _httpClientContext.getCookieStore().getCookies())
        {
            if (X_LABKEY_CSRF.equals(c.getName()))
                _csrf = c.getValue();

            if (JSESSIONID.equals(c.getName()))
                _sessionId = c.getValue();
        }
    }

    /**
     * Ensures that the credentials have been used to authenticate the users and returns a client that can be used for other requests
     * @return an HTTP client
     * @throws IOException if there is an IO problem executing the command to ensure login
     * @throws CommandException if the server returned a non-success status code.
     */
    public CloseableHttpClient ensureAuthenticated() throws IOException, CommandException
    {
        EnsureLoginCommand command = new EnsureLoginCommand();
        CommandResponse response = command.execute(this, "/home");
        return getHttpClient();
    }

    /**
     * Invalidates the current HTTP session on the server, if any
     * @return an HTTP client
     * @throws IOException if there is an IO problem executing the command to ensure logout
     * @throws CommandException if the server returned a non-success status code.
     */
    public CloseableHttpClient logout() throws IOException, CommandException
    {
        LogoutCommand command = new LogoutCommand();
        CommandResponse response = command.execute(this, "/home");
        return getHttpClient();
    }

    /**
     * For site-admins only, start impersonating a user.
     *
     * Admins may impersonate other users to perform actions on their behalf.
     * Site admins may impersonate any user in any project. Project admins must
     * provide a <code>projectPath</code> and may only impersonate within the
     * project in which they have admin permission.
     *
     * @param email The user to impersonate
     * @see Connection#stopImpersonate()
     * @return this connection
     * @throws IOException Thrown if there was an IO problem.
     * @throws CommandException if the server returned a non-success status code.
     */
    public Connection impersonate(/*@NotNull*/ String email) throws IOException, CommandException
    {
        return impersonate(email, null);
    }

    /**
     * For site-admins or project-admins only, start impersonating a user.
     *
     * Admins may impersonate other users to perform actions on their behalf.
     * Site admins may impersonate any user in any project. Project admins must
     * provide a <code>projectPath</code> and may only impersonate within the
     * project in which they have admin permission.
     *
     * @param email The user to impersonate
     * @param projectPath The project path within which the user will be impersonated.
     * @see Connection#stopImpersonate()
     * @return this connection
     * @throws IOException Thrown if there was an IO problem.
     * @throws CommandException if the server returned a non-success status code.
     */
    public Connection impersonate(/*@NotNull*/ String email, /*@Nullable*/ String projectPath) throws IOException, CommandException
    {
        Objects.requireNonNull(email, "email");

        CommandResponse resp = new ImpersonateUserCommand(email).execute(this, projectPath);
        if (resp.getStatusCode() != 200)
            throw new CommandException("Failed to impersonate user");

        _impersonateUser = email;
        _impersonatePath = projectPath;
        return this;
    }

    /**
     * Stop impersonating a user.
     * @return this connection
     * @throws IOException Thrown if there was an IO problem.
     * @throws CommandException if the server returned a non-success status code.
     */
    public Connection stopImpersonate() throws IOException, CommandException
    {
        if (_impersonateUser != null)
        {
            CommandResponse resp = new StopImpersonatingCommand().execute(this, _impersonatePath);

            // on success, a 302 redirect response is returned
            if (resp.getStatusCode() != 302)
                throw new CommandException("Failed to stop impersonating");

            _impersonateUser = null;
            _impersonatePath = null;
        }

        return this;
    }

    CloseableHttpResponse executeRequest(HttpUriRequest request, Integer timeout) throws IOException, URISyntaxException, AuthenticationException
    {
        // Delegate authentication setup to CredentialsProvider
        _credentialsProvider.configureRequest(getBaseURI(), request, _httpClientContext);

        CloseableHttpClient client = getHttpClient();

        // Set the timeout on the request if it is different the client's default
        if (request instanceof HttpRequestBase && timeout != null && timeout != getTimeout())
        {
            HttpRequestBase r = (HttpRequestBase)request;
            RequestConfig base = r.getConfig();
            if (base == null)
                base = RequestConfig.DEFAULT;
            r.setConfig(RequestConfig.copy(base).setSocketTimeout(timeout).build());
        }

        beforeExecute(request);
        CloseableHttpResponse response = client.execute(request, _httpClientContext);
        afterExecute();

        return response;
    }

    /**
     * Set a default timeout for Commands that have not established their own timeouts. Null resets the Connection to the
     * default timeout (60 seconds). 0 means the request should never timeout.
     * NOTE: Changing this setting will force the underlying http client to be recreated.
     *
     * @param timeout the length of the timeout waiting for the server response, in milliseconds
     * @return this connection
     */
    public Connection setTimeout(Integer timeout)
    {
        _timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
        _client = null;
        return this;
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
     * Sets the proxy host and port for this Connection.
     * NOTE: Changing this setting will force the underlying http client to be recreated.
     * @param host the proxy host
     * @param port the proxy port
     * @return this connection
     */
    public Connection setProxy(String host, Integer port)
    {
        _proxyHost = host;
        _proxyPort = port;
        _client = null;
        return this;
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
     * NOTE: Changing this setting will force the underlying http client to be recreated.
     *
     * @param acceptSelfSignedCerts set to false to not accept self-signed certificates
     * @return this connection
     */
    public Connection setAcceptSelfSignedCerts(boolean acceptSelfSignedCerts)
    {
        // Handled in getHttpClient using 4.3.x approach documented here http://stackoverflow.com/questions/19517538/ignoring-ssl-certificate-in-apache-httpclient-4-3
        _acceptSelfSignedCerts = acceptSelfSignedCerts;
        _client = null;
        return this;
    }

    /**
     * @param name The cookie name
     * @param value The cookie value
     * @param domain The domain to which the cookie is visible
     * @param path The path to which the cookie is visible
     * @param expiry The cookie's expiration date
     * @param isSecure Whether the cookie requires a secure connection
     * @return this connection
     */
    public Connection addCookie(String name, String value, String domain, String path, Date expiry, boolean isSecure)
    {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setExpiryDate(expiry);
        cookie.setSecure(isSecure);
        _httpClientContext.getCookieStore().addCookie(cookie);

        // Don't use session info from different server
        if (combineHostPath(_baseURI.getHost(), _baseURI.getPath()).equals(combineHostPath(domain, path)))
        {
            if (X_LABKEY_CSRF.equals(name))
                _csrf = value;
            if (JSESSIONID.equals(name))
                _sessionId = value;
        }

        return this;
    }

    private String combineHostPath(String host, String path)
    {
        String hostPath = (host == null ? "" : host.trim()) + (path == null ? "" : path.trim());
        return hostPath.replaceAll("//+", "/").replaceFirst("/$", "");
    }

    /**
     * Utility method to construct a URI without modifying constructor signature
     */
    private static URI toURI(String baseUrl)
    {
        try
        {
            return new URI(baseUrl);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Invalid target server URL: " + baseUrl);
        }
    }
}