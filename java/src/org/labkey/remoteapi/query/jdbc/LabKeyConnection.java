/*
 * Copyright (c) 2010-2017 LabKey Corporation
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


import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class LabKeyConnection extends BaseJDBC implements java.sql.Connection
{
    private final static Logger _log = Logger.getLogger("test");

    public static String TIMEOUT_PARAMETER_NAME = "Timeout";

    private final org.labkey.remoteapi.Connection _conn;
    private String _folderPath;
    private Properties _clientInfo;
    private boolean _closed = false;

    private Map<String, Class<?>> _typeMap = new HashMap<>();

    public LabKeyConnection(org.labkey.remoteapi.Connection conn)
    {
        _conn = conn;
    }

    public org.labkey.remoteapi.Connection getConnection()
    {
        return _conn;
    }

    public String getFolderPath()
    {
        return _folderPath;
    }

    public void setFolderPath(String folderPath)
    {
        _folderPath = folderPath;
    }

    public void clearWarnings() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement() throws SQLException
    {
        return new LabKeyStatement(this);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String nativeSQL(String sql) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        validateOpen();
        if (!autoCommit)
        {
            throw new UnsupportedOperationException();
        }
    }

    public boolean getAutoCommit() throws SQLException
    {
        return true;
    }

    private void validateOpen() throws SQLException
    {
        if (_closed)
        {
            throw new SQLException("Connection has already been closed");
        }
    }

    public void commit() throws SQLException
    {
        validateOpen();
        throw new UnsupportedOperationException();
    }

    public void rollback() throws SQLException
    {
        validateOpen();
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException
    {
        _closed = true;
    }

    public boolean isClosed() throws SQLException
    {
        return _closed;
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        validateOpen();
        return new LabKeyDatabaseMetaData(this);
    }

    public void setReadOnly(boolean readOnly) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly() throws SQLException
    {
        return false;
    }

    public void setCatalog(String catalog) throws SQLException
    {
        _log.info("setCatalog: " + catalog);
        _folderPath = catalog;
    }

    public String getCatalog() throws SQLException
    {
        validateOpen();
        return _folderPath;
    }

    public void setTransactionIsolation(int level) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getTransactionIsolation() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return _typeMap;
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
        _typeMap = new HashMap<>(map);
    }

    public void setHoldability(int holdability) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getHoldability() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint(String name) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void rollback(Savepoint savepoint) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob createClob() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob createBlob() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public NClob createNClob() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLXML createSQLXML() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isValid(int timeout) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException
    {
        _clientInfo.put(name, value);
        pushTimeout();
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException
    {
        _clientInfo = properties;
        pushTimeout();
    }

    private void pushTimeout()
    {
        if (_clientInfo.containsKey(TIMEOUT_PARAMETER_NAME))
        {
            String timeoutString = _clientInfo.getProperty(TIMEOUT_PARAMETER_NAME);
            _log.log(Level.INFO, "Setting timeout on connection to " + timeoutString);
            if (timeoutString == null || timeoutString.trim().isEmpty())
            {
                _conn.setTimeout(null);
            }
            else
            {
                try
                {
                    int timeout = Integer.parseInt(timeoutString);
                    _conn.setTimeout(timeout);
                }
                catch (NumberFormatException e)
                {
                    _conn.setTimeout(null);
                }
            }
        }
    }

    public String getClientInfo(String name) throws SQLException
    {
        return _clientInfo.getProperty(name);
    }

    public Properties getClientInfo() throws SQLException
    {
        return _clientInfo;
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    // These JDBC 4.1 methods must be "implemented" so JDK 7 can compile this class.

    public void setSchema(String schema) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getSchema() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void abort(Executor executor) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getNetworkTimeout() throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
