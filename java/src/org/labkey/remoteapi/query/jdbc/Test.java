package org.labkey.remoteapi.query.jdbc;

import java.sql.*;
import java.util.Properties;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class Test
{
    public static void main(String... args) throws Exception
    {
        Driver driver = new LabKeyDriver();
        Connection jdbcConnection = driver.connect("http://localhost/labkey", new Properties());
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
