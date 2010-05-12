/*
 * Copyright (c) 2010 LabKey Corporation
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
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * User: jeckels
 * Date: Apr 5, 2010
 */
public class LabKeyDriver implements Driver
{
    public boolean acceptsURL(String url) throws SQLException
    {
        return true;
    }

    public Connection connect(String url, Properties info) throws SQLException
    {
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
        return new LabKeyConnection(connection);
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
}
