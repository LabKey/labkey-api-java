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

import java.sql.*;
import java.util.Properties;

/**
 * Simple test program for prototype LabKey JDBC driver
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class Test
{
    public static void main(String... args) throws Exception
    {
        Driver driver = new LabKeyDriver();
        Properties props = new Properties();
        props.put("user", "jeckels@labkey.com");
        props.put("password", "SuperSecurePassword!");
        Connection jdbcConnection = driver.connect("http://localhost:8080/labkey/home", props);
//        Statement statement = jdbcConnection.createStatement();
//        ResultSet resultSet = statement.executeQuery("SELECT p.Fraction, p.TrimmedPeptide FROM ms2.Peptides p");
        ResultSet resultSet = jdbcConnection.getMetaData().getSchemas();
        while (resultSet.next())
        {
            System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
        }
        ResultSet tablesRS = jdbcConnection.getMetaData().getTables(null, "exp", null, null);
        while (tablesRS.next())
        {
            System.out.println(tablesRS.getString(3));
        }
        ResultSet columnsRS = jdbcConnection.getMetaData().getColumns(null, "exp", "Datas", null);
        while (columnsRS.next())
        {
            System.out.println(columnsRS.getString("table_schem") + "." + columnsRS.getString(3) + "." + columnsRS.getString(4));
        }
    }
}
