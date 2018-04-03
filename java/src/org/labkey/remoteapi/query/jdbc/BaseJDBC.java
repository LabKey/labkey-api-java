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

import java.sql.SQLException;
import java.sql.Types;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.Date;

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

    /**
     * Consolidation to map the data types in LabKey Server API responses to appropriate SQL or java types.
     * Logic migrated from LabKeyResultSetMetadata.getColumnType(), LabKeyResultSetMetadata.getColumnTypeName(),
     * LabKeyDatabaseMetaData.getSQLType(), and LabKeyResultSet.extractColumns().
     *
     * Note the type identified in a GetQueryDetails call may be different than for a SelectRows(ExecuteSql) call,
     * (double vs float, "int" vs "Integer"). If these should be unified in the future, some of the mappings here may need
     * to change. The goal would be to have as few overrides to the matchOn* methods as possible.
     */
    enum JdbcType
    {
        Integer(Types.INTEGER, Integer.class)
                {
                    @Override
                    protected boolean matchOnJavaType(Class type)
                    {
                        return Arrays.asList(Integer.class, Short.class, Byte.class).contains(type);
                    }

                    @Override
                    protected boolean matchOnSelectRowsType(String type)
                    {
                        return "int".equalsIgnoreCase(type);
                    }
                },
        BigInt(Types.BIGINT, Long.class)
                {
                    @Override
                    protected boolean matchOnSelectRowsType(String type)
                    {
                        return false;
                    }

                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return false;
                    }
                },
        Varchar(Types.VARCHAR, String.class)
                {
                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return type.contains("String");
                    }
                },
        Boolean(Types.BOOLEAN, Boolean.class)
                {
                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return type.contains("Boolean");
                    }
                },
        Timestamp(Types.TIMESTAMP, Date.class)
                {
                    @Override
                    protected boolean matchOnJavaType(Class type)
                    {
                        return Date.class.isAssignableFrom(type); // java.util.Date is super to java.sql.Date, java.sql.Time, and java.sql.Timestamp
                    }

                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return type.contains("Date");
                    }
                },
        Float(Types.FLOAT, Float.class)
                {
                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return false;
                    }
                },
        Double(Types.DOUBLE, Double.class)
                {
                    @Override
                    protected boolean matchOnSelectRowsType(String type)
                    {
                        return false;
                    }

                    @Override
                    protected boolean matchOnQueryDetailsType(String type)
                    {
                        return type.contains("Number");
                    }
                };

        private final int _sqlType;
        private final Class _javaType;
        JdbcType(int sqlType, Class type)
        {
            _sqlType = sqlType;
            _javaType = type;
        }

        protected boolean matchOnJavaType(Class type)
        {
            return type == _javaType;
        }

        protected boolean matchOnQueryDetailsType(String type)
        {
            return matchOnSimpleName(_javaType, type);
        }

        protected boolean matchOnSelectRowsType(String type)
        {
            return matchOnSimpleName(_javaType, type);
        }

        private boolean matchOnSimpleName(Class javaType, String type)
        {
            return javaType.getSimpleName().equalsIgnoreCase(type);
        }

        static int getSqlType(String typeName)
        {
            for (JdbcType value : values())
            {
                if (value.matchOnQueryDetailsType(typeName))
                    return value._sqlType;
            }
            return Types.VARCHAR;
        }

        static int getSqlType(Class type)
        {
            for (JdbcType value : values())
            {
                if (value.matchOnJavaType(type))
                    return value._sqlType;
            }
            throw new LoggingUnsupportedOperationException("Unexpected type: " + type);
        }

        static String getSqlTypeName(Class type)
        {
            for (JdbcType value : values())
            {
                if (value.matchOnJavaType(type))
                    return value.name().toUpperCase();
            }
            throw new LoggingUnsupportedOperationException("Unexpected type: " + type);
        }

        static Class getJavaType(String typeName)
        {
            for (JdbcType value : values())
            {
                if (value.matchOnSelectRowsType(typeName))
                    return value._javaType;
            }
            return Object.class;
        }
    }

}
