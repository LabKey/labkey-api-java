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

import org.labkey.remoteapi.query.SelectRowsResponse;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class LabKeyResultSet extends BaseJDBC implements ResultSet
{
    protected final List<Map<String, Object>> _rows;
    private final List<Column> _columns;
    private final Map<String, Column> _columnMap;
    private final LabKeyConnection _connection;
    private int _currentRowIndex = -1;
    private boolean _closed = false;
    private Object _lastValue = null;

    public LabKeyResultSet(List<Map<String, Object>> rows, List<Column> cols, LabKeyConnection connection)
    {
        _rows = rows;
        _columns = cols;
        _connection = connection;
        _columnMap = new HashMap<>();
        for (Column column : _columns)
        {
            _columnMap.put(column.getName().toLowerCase(), column);
        }
    }

    public LabKeyResultSet(SelectRowsResponse response, LabKeyConnection connection)
    {
        this(response.getRows(), extractColumns(response), connection);
    }

    private static List<Column> extractColumns(SelectRowsResponse response)
    {
        List<Column> result = new ArrayList<>();
        for (Map<String, Object> metadata : (List<Map<String, Object>>)response.getMetaData().get("fields"))
        {
            String name = (String) metadata.get("name");
            String typeName = (String) metadata.get("type");
            Class type = Object.class;
            if ("int".equalsIgnoreCase(typeName))
            {
                type = Integer.class;
            }
            else if ("date".equalsIgnoreCase(typeName))
            {
                type = Date.class;
            }
            else if ("string".equalsIgnoreCase(typeName))
            {
                type = String.class;
            }
            else if ("float".equalsIgnoreCase(typeName))
            {
                type = Float.class;
            }
            else if ("boolean".equalsIgnoreCase(typeName))
            {
                type = Boolean.class;
            }
            Column column = new Column(name, type);
            result.add(column);
        }
        return result;
    }

    public static class Column
    {
        private final String _name;
        private final Class _type;

        public Column(String name, Class type)
        {
            _name = name;
            _type = type;
        }

        public String getName()
        {
            return _name;
        }

        public Class getType()
        {
            return _type;
        }
    }

    public boolean absolute(int row) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean next() throws SQLException
    {
        if (_currentRowIndex < _rows.size() - 1)
        {
            _currentRowIndex++;
            return true;
        }
        return false;
    }

    public void close() throws SQLException
    {
        _closed = true;
    }

    public boolean wasNull() throws SQLException
    {
        validateRow();
        return _lastValue == null;
    }

    private Number getNumber(String columnLabel) throws SQLException
    {
        Object result = getObject(columnLabel);
        if (result == null || result instanceof Number)
        {
            return (Number)result;
        }
        else
        {
            throw new SQLException(columnLabel + " is a non-numeric value (" + result.getClass() + ")");
        }
    }

    public String getString(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getString(column.getName());
    }

    private Column getColumn(int columnIndex) throws SQLException
    {
        if (columnIndex < 1 || columnIndex > _columns.size())
        {
            throw new SQLException("Invalid column index: " + columnIndex);
        }
        return _columns.get(columnIndex - 1);
    }

    public boolean getBoolean(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getBoolean(column.getName());
    }

    public byte getByte(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getByte(column.getName());
    }

    public short getShort(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getShort(column.getName());
    }

    public int getInt(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getInt(column.getName());
    }

    public long getLong(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getLong(column.getName());
    }

    public float getFloat(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getFloat(column.getName());
    }

    public double getDouble(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getDouble(column.getName());
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getBigDecimal(column.getName());
    }

    public byte[] getBytes(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getBytes(column.getName());
    }

    public Date getDate(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getDate(column.getName());
    }

    public Time getTime(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getTime(column.getName());
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getTimestamp(column.getName());
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getAsciiStream(column.getName());
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getUnicodeStream(column.getName());
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getBinaryStream(column.getName());
    }

    private void validateOpen() throws SQLException
    {
        if (_closed)
        {
            throw new SQLException("ResultSet has already been closed");
        }
    }

    private Map<String, Object> validateRow() throws SQLException
    {
        validateOpen();
        if (_currentRowIndex < 0 || _currentRowIndex > _rows.size())
        {
            throw new SQLException("No active row");
        }
        return _rows.get(_currentRowIndex);
    }

    public String getString(String columnLabel) throws SQLException
    {
        Object value = getObject(columnLabel);
        return value == null ? null : value.toString();
    }

    public boolean getBoolean(String columnLabel) throws SQLException
    {
        Object result = getObject(columnLabel);
        if (result == null)
        {
            return false;
        }
        if (result instanceof Boolean)
        {
            return ((Boolean)result).booleanValue();
        }
        else
        {
            throw new SQLException(columnLabel + " is a non-numeric value (" + result.getClass() + ")");
        }
    }

    public byte getByte(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.byteValue();
    }

    public short getShort(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.shortValue();
    }

    public int getInt(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.intValue();
    }

    public long getLong(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.longValue();
    }

    public float getFloat(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.floatValue();
    }

    public double getDouble(String columnLabel) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? 0 : result.doubleValue();
    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException
    {
        Number result = getNumber(columnLabel);
        return result == null ? null : new BigDecimal(result.doubleValue());
    }

    public byte[] getBytes(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    public void clearWarnings() throws SQLException
    {
        // No-op, we don't do anything with warnings
    }

    public String getCursorName() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        return new LabKeyResultSetMetaData(_columns, _connection);
    }

    public Object getObject(int columnIndex) throws SQLException
    {
        Column column = getColumn(columnIndex);
        return getObject(column.getName());
    }

    public Object getObject(String columnLabel) throws SQLException
    {
        Map<String, Object> map = validateRow();
        Column column = _columnMap.get(columnLabel.toLowerCase());
        if (column != null)
        {
            _lastValue = map.get(column.getName());
            return _lastValue;
        }
        throw new SQLException(columnLabel + " not found");
    }

    public int findColumn(String columnLabel) throws SQLException
    {
        for (int i = 0; i < _columns.size(); i++)
        {
            if (_columns.get(i).getName().equalsIgnoreCase(columnLabel))
            {
                return i + 1;
            }
        }
        throw new SQLException("Unable to find column: " + columnLabel);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isBeforeFirst() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isAfterLast() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isFirst() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isLast() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void beforeFirst() throws SQLException
    {
        validateOpen();
        _currentRowIndex = -1;
    }

    public void afterLast() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean first() throws SQLException
    {
        validateOpen();
        if (_rows.size() > 0)
        {
            _currentRowIndex = 0;
            return true;
        }
        return false;
    }

    public boolean last() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getRow() throws SQLException
    {
        return _currentRowIndex + 1;
    }

    public boolean relative(int rows) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean previous() throws SQLException
    {
        throw new UnsupportedOperationException();
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

    }

    public int getFetchSize() throws SQLException
    {
        return 0;
    }

    public int getType() throws SQLException
    {
        return TYPE_SCROLL_INSENSITIVE;
    }

    public int getConcurrency() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean rowUpdated() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean rowInserted() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean rowDeleted() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNull(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateShort(int columnIndex, short x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateInt(int columnIndex, int x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateLong(int columnIndex, long x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateString(int columnIndex, String x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDate(int columnIndex, Date x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateTime(int columnIndex, Time x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNull(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateByte(String columnLabel, byte x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateShort(String columnLabel, short x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateInt(String columnLabel, int x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateLong(String columnLabel, long x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(String columnLabel, float x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(String columnLabel, double x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateString(String columnLabel, String x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDate(String columnLabel, Date x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateTime(String columnLabel, Time x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void insertRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void deleteRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void refreshRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void cancelRowUpdates() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void moveToInsertRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void moveToCurrentRow() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Statement getStatement() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Array getArray(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Array getArray(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateArray(int columnIndex, Array x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateArray(String columnLabel, Array x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getHoldability() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean isClosed() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNString(int columnIndex, String nString) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNString(String columnLabel, String nString) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getNString(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getNString(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    // These JDBC 4.1 methods must be "implemented" so JDK 7 can compile this class.

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
