/*
 * Copyright (c) 2010-2012 LabKey Corporation
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

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.query.ExecuteSqlCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class LabKeyStatement implements CallableStatement
{
    private final LabKeyConnection _connection;
    private boolean _closed = false;
    private ResultSet _nextResultSet;
    private int _maxRows = 0;

    public LabKeyStatement(LabKeyConnection connection)
    {
        _connection = connection;
    }

    private void validateOpen() throws SQLException
    {
        if (_closed)
        {
            throw new SQLException("Statement is closed");
        }
    }

    public Array getArray(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean wasNull() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getString(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte getByte(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public short getShort(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getInt(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public long getLong(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public float getFloat(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public double getDouble(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setURL(String parameterName, URL val) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNull(String parameterName, int sqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setByte(String parameterName, byte x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setShort(String parameterName, short x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setInt(String parameterName, int x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setLong(String parameterName, long x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setFloat(String parameterName, float x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDouble(String parameterName, double x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setString(String parameterName, String x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDate(String parameterName, Date x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTime(String parameterName, Time x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(String parameterName, Object x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getString(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte getByte(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public short getShort(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getInt(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public long getLong(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public float getFloat(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public double getDouble(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Array getArray(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setRowId(String parameterName, RowId x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNString(String parameterName, String value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(String parameterName, NClob value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getNString(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getNString(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(String parameterName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(String parameterName, Blob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(String parameterName, Clob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(String parameterName, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet executeQuery() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setByte(int parameterIndex, byte x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setShort(int parameterIndex, short x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setInt(int parameterIndex, int x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setLong(int parameterIndex, long x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setFloat(int parameterIndex, float x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDouble(int parameterIndex, double x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setString(int parameterIndex, String x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDate(int parameterIndex, Date x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTime(int parameterIndex, Time x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void clearParameters() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(int parameterIndex, Object x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void addBatch() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setArray(int parameterIndex, Array x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setURL(int parameterIndex, URL x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNString(int parameterIndex, String value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {
        ExecuteSqlCommand command = new ExecuteSqlCommand("exp", sql);
        try
        {
            if (_maxRows != 0)
            {
                command.setMaxRows(_maxRows);
            }
            SelectRowsResponse response = command.execute(_connection.getConnection(), _connection.getFolderPath());
            return new LabKeyResultSet(response, _connection);
        }
        catch (IOException e)
        {
            throw new SQLException(e);
        }
        catch (CommandException e)
        {
            throw new SQLException(e);
        }
    }

    public int executeUpdate(String sql) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException
    {
        _closed = true;
    }

    public int getMaxFieldSize() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setMaxFieldSize(int max) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getMaxRows() throws SQLException
    {
        return _maxRows;
    }

    public void setMaxRows(int max) throws SQLException
    {
        _maxRows = max;
    }

    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getQueryTimeout() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setQueryTimeout(int seconds) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void cancel() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    public void clearWarnings() throws SQLException
    {
        // No-op - we don't handle warnings
    }

    public void setCursorName(String name) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql) throws SQLException
    {
        _nextResultSet = executeQuery(sql);
        return true;
    }

    public ResultSet getResultSet() throws SQLException
    {
        validateOpen();
        if (_nextResultSet == null)
        {
            throw new SQLException("No available ResultSet");
        }
        ResultSet result = _nextResultSet;
        _nextResultSet = null;
        return result;
    }

    public int getUpdateCount() throws SQLException
    {
        validateOpen();
        return 0;
    }

    public boolean getMoreResults() throws SQLException
    {
        validateOpen();
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getFetchDirection() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setFetchSize(int rows) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getFetchSize() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getResultSetConcurrency() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getResultSetType() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void addBatch(String sql) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void clearBatch() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int[] executeBatch() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Connection getConnection() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean getMoreResults(int current) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getGeneratedKeys() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getResultSetHoldability() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isClosed() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setPoolable(boolean poolable) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isPoolable() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    // These JDBC 4.1 methods must be "implemented" so JDK 7 can compile this class.

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void closeOnCompletion() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isCloseOnCompletion() throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
