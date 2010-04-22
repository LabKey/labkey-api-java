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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class LabKeyResultSetMetaData extends BaseJDBC implements ResultSetMetaData 
{
    private final List<LabKeyResultSet.Column> _columns;
    private final LabKeyConnection _connection;

    public LabKeyResultSetMetaData(List<LabKeyResultSet.Column> columns, LabKeyConnection connection)
    {
        _columns = columns;
        _connection = connection;
    }

    public String getCatalogName(int column) throws SQLException
    {
        return _connection.getFolderPath();
    }

    public int getColumnCount() throws SQLException
    {
        return _columns.size();
    }

    public boolean isAutoIncrement(int column) throws SQLException
    {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException
    {
        return true;
    }

    public boolean isSearchable(int column) throws SQLException
    {
        return true;
    }

    public boolean isCurrency(int column) throws SQLException
    {
        return false;
    }

    public int isNullable(int column) throws SQLException
    {
        return ResultSetMetaData.columnNullable;
    }

    public boolean isSigned(int column) throws SQLException
    {
        return true;
    }

    public int getColumnDisplaySize(int column) throws SQLException
    {
        return 10;
    }

    public String getColumnLabel(int column) throws SQLException
    {
        return getColumn(column).getName();
    }

    public String getColumnName(int column) throws SQLException
    {
        return getColumn(column).getName();
    }

    public String getSchemaName(int column) throws SQLException
    {
        return "unknown";
    }

    public int getPrecision(int column) throws SQLException
    {
        return 4;
    }

    public int getScale(int column) throws SQLException
    {
        return 4;
    }

    public String getTableName(int column) throws SQLException
    {
        return "unknown";
    }

    public int getColumnType(int column) throws SQLException
    {
        Class type = getColumn(column).getType();
        if (type == String.class)
        {
            return Types.VARCHAR;
        }
        if (type == Integer.class)
        {
            return Types.INTEGER;
        }
        if (type == Long.class)
        {
            return Types.BIGINT;
        }
        if (type == Date.class)
        {
            return Types.DATE;
        }
        if (type == Short.class)
        {
            return Types.INTEGER;
        }
        if (type == Byte.class)
        {
            return Types.INTEGER;
        }
        if (type == Double.class)
        {
            return Types.DOUBLE;
        }
        if (type == Float.class)
        {
            return Types.FLOAT;
        }
        if (type == Boolean.class)
        {
            return Types.BOOLEAN;
        }
        throw new UnsupportedOperationException("Unexpected type: " + type);
    }

    public String getColumnTypeName(int column) throws SQLException
    {
        Class type = getColumn(column).getType();
        if (type == String.class)
        {
            return "VARCHAR";
        }
        if (type == Integer.class)
        {
            return "INTEGER";
        }
        if (type == Long.class)
        {
            return "BIGINT";
        }
        if (type == Date.class)
        {
            return "DATE";
        }
        if (type == Short.class)
        {
            return "INTEGER";
        }
        if (type == Byte.class)
        {
            return "INTEGER";
        }
        if (type == Double.class)
        {
            return "DOUBLE";
        }
        if (type == Float.class)
        {
            return "FLOAT";
        }
        if (type == Boolean.class)
        {
            return "BOOLEAN";
        }
        throw new UnsupportedOperationException("Unexpected type: " + type);
    }

    private LabKeyResultSet.Column getColumn(int column)
    {
        return _columns.get(column - 1);
    }

    public boolean isReadOnly(int column) throws SQLException
    {
        return true;
    }

    public boolean isWritable(int column) throws SQLException
    {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException
    {
        return false;
    }

    public String getColumnClassName(int column) throws SQLException
    {
        return getColumn(column).getType().getName();
    }
}
