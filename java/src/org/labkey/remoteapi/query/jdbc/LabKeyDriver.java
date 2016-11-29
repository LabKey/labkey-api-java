/*
 * Copyright (c) 2010-2016 LabKey Corporation
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
package org.labkey.remoteapi.query.jdbc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * User: jeckels
 * Date: Apr 5, 2010
 */
public class LabKeyDriver implements Driver
{
    private static final String URL_PREFIX = "jdbc:labkey:";

    static
    {
        try
        {
            DriverManager.registerDriver(new LabKeyDriver());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean acceptsURL(String url) throws SQLException
    {
        if (url == null)
        {
            return false;
        }

        url = url.toLowerCase();
        return url.startsWith(URL_PREFIX) || url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * Supports using a # to preset the initial folder path for the connection. For example,
     * jdbc:labkey:http://localhost:8080/labkey#/MyProject, will target MyProject by default, even if no catalog is
     * set on the connection
     */
    public Connection connect(String rawURL, Properties info) throws SQLException
    {
        if (rawURL.startsWith(URL_PREFIX))
        {
            rawURL = rawURL.substring(URL_PREFIX.length());
        }
        String[] parsed = rawURL.split("#");
        String baseURL = parsed[0];
        String folderPath = null;
        if (parsed.length > 2)
        {
            throw new IllegalArgumentException("Illegal URL - more than one # - " + rawURL);
        }
        if (parsed.length > 1)
        {
            folderPath = parsed[1];
        }
        String user = info.getProperty("user");
        String password = info.getProperty("password");
        org.labkey.remoteapi.Connection connection;
        if (user != null && password != null)
        {
            connection = new org.labkey.remoteapi.Connection(baseURL, user, password);
        }
        else
        {
            try
            {
                connection = new org.labkey.remoteapi.Connection(baseURL);
            }
            catch (URISyntaxException | IOException e)
            {
                throw new SQLException(e);
            }
        }
        LabKeyConnection labKeyConnection = new LabKeyConnection(connection);
        labKeyConnection.setClientInfo(info);
        labKeyConnection.setFolderPath(folderPath);
        return labKeyConnection;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
    {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion()
    {
        return 0;
    }

    public int getMinorVersion()
    {
        return 0;
    }

    public boolean jdbcCompliant()
    {
        return false;
    }

    // This JDBC 4.1 method must be "implemented" so JDK 7 can compile this class.

    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        throw new UnsupportedOperationException();
    }
}
