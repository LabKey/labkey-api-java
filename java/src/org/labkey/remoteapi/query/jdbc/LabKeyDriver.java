/*
 * Copyright (c) 2010-2014 LabKey Corporation
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

        return url.toLowerCase().startsWith(URL_PREFIX);
    }

    public Connection connect(String url, Properties info) throws SQLException
    {
        if (url.startsWith(URL_PREFIX))
        {
            url = url.substring(URL_PREFIX.length());
        }
        String user = info.getProperty("user");
        String password = info.getProperty("password");
        org.labkey.remoteapi.Connection connection;
        if (user != null && password != null)
        {
            connection = new org.labkey.remoteapi.Connection(url, user, password);
        }
        else
        {
            connection = new org.labkey.remoteapi.Connection(url);
        }
        LabKeyConnection labKeyConnection = new LabKeyConnection(connection);
        labKeyConnection.setClientInfo(info);
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
