package org.labkey.remoteapi.query.jdbc;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class BaseJDBC implements Wrapper
{
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        throw new SQLException("Not a wrapper for " + iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }
}
