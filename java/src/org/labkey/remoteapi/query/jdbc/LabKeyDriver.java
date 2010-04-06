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
        org.labkey.remoteapi.Connection connection = new org.labkey.remoteapi.Connection(url);
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
