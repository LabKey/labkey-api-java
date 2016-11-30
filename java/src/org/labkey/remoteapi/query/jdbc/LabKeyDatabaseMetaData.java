/*
 * Copyright (c) 2010-2016 LabKey Corporation
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
import org.labkey.remoteapi.query.*;
import org.labkey.remoteapi.security.GetContainersCommand;
import org.labkey.remoteapi.security.GetContainersResponse;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: jeckels
 * Date: Apr 2, 2010
 */
public class LabKeyDatabaseMetaData extends BaseJDBC implements DatabaseMetaData
{
    private final static Logger _log = Logger.getLogger("test");

    private final LabKeyConnection _connection;

    private Map<String, GetQueryDetailsResponse> _queryDetails = new HashMap<>();

    public LabKeyDatabaseMetaData(LabKeyConnection connection)
    {
        _connection = connection;
    }

    public boolean allProceduresAreCallable() throws SQLException
    {
        return true;
    }

    public boolean allTablesAreSelectable() throws SQLException
    {
        return true;
    }

    public String getURL() throws SQLException
    {
        return _connection.getConnection().getBaseUrl();
    }

    public String getUserName() throws SQLException
    {
        return null;  // TODO: Could get from _connection CredentialsProvider, but need to accommodate no email situations (guest auth and API keys)
    }

    public boolean isReadOnly() throws SQLException
    {
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException
    {
        return true; // TODO - get real value from DB
    }

    public boolean nullsAreSortedLow() throws SQLException
    {
        return false; // TODO - get real value from DB
    }

    public boolean nullsAreSortedAtStart() throws SQLException
    {
        return true; // TODO - get real value from DB
    }

    public boolean nullsAreSortedAtEnd() throws SQLException
    {
        return false; // TODO - get real value from DB
    }

    public String getDatabaseProductName() throws SQLException
    {
        return "LabKey Server";
    }

    public String getDatabaseProductVersion() throws SQLException
    {
        return "0.01";
    }

    public String getDriverName() throws SQLException
    {
        return "LabKey";
    }

    public String getDriverVersion() throws SQLException
    {
        return "0.01";
    }

    public int getDriverMajorVersion()
    {
        return 0;
    }

    public int getDriverMinorVersion()
    {
        return 1;
    }

    public boolean usesLocalFiles() throws SQLException
    {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException
    {
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException
    {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException
    {
        return false;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException
    {
        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException
    {
        return true;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
    {
        return true;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
    {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
    {
        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
    {
        return true;
    }

    public String getIdentifierQuoteString() throws SQLException
    {
        return "\"";
    }

    public String getSQLKeywords() throws SQLException
    {
        return "";
    }

    public String getNumericFunctions() throws SQLException
    {
        return ""; // TODO - fill in
    }

    public String getStringFunctions() throws SQLException
    {
        return ""; // TODO - fill in
    }

    public String getSystemFunctions() throws SQLException
    {
        return ""; // TODO - fill in
    }

    public String getTimeDateFunctions() throws SQLException
    {
        return ""; // TODO - fill in
    }

    public String getSearchStringEscape() throws SQLException
    {
        return "%"; // TODO - fill in
    }

    public String getExtraNameCharacters() throws SQLException
    {
        return "";
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException
    {
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException
    {
        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException
    {
        return true;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException
    {
        return true; // TODO - fill in
    }

    public boolean supportsConvert() throws SQLException
    {
        return true;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException
    {
        return false; // TODO - fill in
    }

    public boolean supportsTableCorrelationNames() throws SQLException
    {
        return true; // TODO - fill in
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException
    {
        return true;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException
    {
        return true;
    }

    public boolean supportsOrderByUnrelated() throws SQLException
    {
        return true;
    }

    public boolean supportsGroupBy() throws SQLException
    {
        return true;
    }

    public boolean supportsGroupByUnrelated() throws SQLException
    {
        return true;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException
    {
        return true;
    }

    public boolean supportsLikeEscapeClause() throws SQLException
    {
        return true;
    }

    public boolean supportsMultipleResultSets() throws SQLException
    {
        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException
    {
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException
    {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException
    {
        return false;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException
    {
        return false;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException
    {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException
    {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException
    {
        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException
    {
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException
    {
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException
    {
        return true;
    }

    public boolean supportsFullOuterJoins() throws SQLException
    {
        return true;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException
    {
        return true;
    }

    public String getSchemaTerm() throws SQLException
    {
        return "schema";
    }

    public String getProcedureTerm() throws SQLException
    {
        return "function";
    }

    public String getCatalogTerm() throws SQLException
    {
        return "container";
    }

    public boolean isCatalogAtStart() throws SQLException
    {
        return true;
    }

    public String getCatalogSeparator() throws SQLException
    {
        return ".";
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException
    {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException
    {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException
    {
        return true;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException
    {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
    {
        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException
    {
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException
    {
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException
    {
        return true;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException
    {
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
    {
        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException
    {
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException
    {
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException
    {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException
    {
        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException
    {
        return true;
    }

    public boolean supportsSubqueriesInExists() throws SQLException
    {
        return true;
    }

    public boolean supportsSubqueriesInIns() throws SQLException
    {
        return true;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException
    {
        return true;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException
    {
        return true;
    }

    public boolean supportsUnion() throws SQLException
    {
        return true;
    }

    public boolean supportsUnionAll() throws SQLException
    {
        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException
    {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException
    {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException
    {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException
    {
        return false;
    }

    public int getMaxBinaryLiteralLength() throws SQLException
    {
        return 0;
    }

    public int getMaxCharLiteralLength() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnsInGroupBy() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnsInOrderBy() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException
    {
        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException
    {
        return 0;
    }

    public int getMaxConnections() throws SQLException
    {
        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxIndexLength() throws SQLException
    {
        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxProcedureNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxCatalogNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxRowSize() throws SQLException
    {
        return 0;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
    {
        return true;
    }

    public int getMaxStatementLength() throws SQLException
    {
        return 0;
    }

    public int getMaxStatements() throws SQLException
    {
        return 0;
    }

    public int getMaxTableNameLength() throws SQLException
    {
        return 0;
    }

    public int getMaxTablesInSelect() throws SQLException
    {
        return 0;
    }

    public int getMaxUserNameLength() throws SQLException
    {
        return 0;
    }

    public int getDefaultTransactionIsolation() throws SQLException
    {
        return Connection.TRANSACTION_NONE;
    }

    public boolean supportsTransactions() throws SQLException
    {
        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException
    {
        return level == Connection.TRANSACTION_NONE;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException
    {
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException
    {
        return false;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException
    {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException
    {
        return false;
    }

    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException
    {
        _log.log(Level.INFO, "getTables: " + catalog + ", " + schemaPattern + ", " + tableNamePattern);
        _connection.setCatalog(catalog);
        validatePattern(schemaPattern, "schemaPattern");
        if (tableNamePattern != null && !tableNamePattern.equals("%") && (tableNamePattern.contains("%") || tableNamePattern.contains("_")))
        {
            throw new IllegalArgumentException("tableNamePattern must request an exact match, but was: " + tableNamePattern);
        }
        try
        {
            List<String> schemaNames;
            if (schemaPattern != null)
            {
                schemaNames = Collections.singletonList(schemaPattern);
            }
            else
            {
                GetSchemasCommand command = new GetSchemasCommand();
                GetSchemasResponse response = command.execute(_connection.getConnection(), _connection.getFolderPath());
                schemaNames = response.getSchemaNames();
            }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            for (String schemaName : schemaNames)
            {
                GetQueriesCommand command = new GetQueriesCommand(schemaName);
                GetQueriesResponse response = command.execute(_connection.getConnection(), _connection.getFolderPath());

                for (String table : response.getQueryNames())
                {
                    if (matches(table, tableNamePattern))
                    {
                        Map<String, Object> row = new HashMap<String, Object>();
                        row.put("TABLE_CAT", catalog);
                        row.put("TABLE_SCHEM", response.getSchemaName());
                        row.put("TABLE_NAME", table);
                        row.put("TABLE_TYPE", response.isUserDefined(table) ? "VIEW" : "TABLE");
                        row.put("REMARKS", null);
                        row.put("TYPE_CAT", null);
                        row.put("TYPE_SCHEM", null);
                        row.put("TYPE_NAME", null);
                        row.put("SELF_REFERENCING_COL_NAME", null);
                        row.put("REF_GENERATION", null);
                        rows.add(row);
                    }
                }
            }
            List<LabKeyResultSet.Column> columns = new ArrayList<LabKeyResultSet.Column>();
            columns.add(new LabKeyResultSet.Column("TABLE_CAT", String.class));
            columns.add(new LabKeyResultSet.Column("TABLE_SCHEM", String.class));
            columns.add(new LabKeyResultSet.Column("TABLE_NAME", String.class));
            columns.add(new LabKeyResultSet.Column("TABLE_TYPE", String.class));
            columns.add(new LabKeyResultSet.Column("REMARKS", String.class));
            columns.add(new LabKeyResultSet.Column("TYPE_CAT", String.class));
            columns.add(new LabKeyResultSet.Column("TYPE_SCHEM", String.class));
            columns.add(new LabKeyResultSet.Column("TYPE_NAME", String.class));
            columns.add(new LabKeyResultSet.Column("SELF_REFERENCING_COL_NAME", String.class));
            columns.add(new LabKeyResultSet.Column("REF_GENERATION", String.class));
            return new LabKeyResultSet(rows, columns, _connection);
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

    public ResultSet getCatalogs() throws SQLException
    {
        try
        {
            GetContainersCommand command = new GetContainersCommand();
            command.setIncludeSubfolders(true);
            GetContainersResponse response = command.execute(_connection.getConnection(), "/");
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            addCatalog(response.getParsedData(), rows);
            return new LabKeyResultSet(rows, Collections.singletonList(new LabKeyResultSet.Column("TABLE_CAT", String.class)), _connection);
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

    private void addCatalog(Map<String, Object> parsedData, List<Map<String, Object>> rows)
    {
        rows.add(Collections.singletonMap("TABLE_CAT", parsedData.get("path")));
        if (parsedData.containsKey("children"))
        {
            List<Map<String, Object>> children = (List<Map<String, Object>>)parsedData.get("children");
            for (Map<String, Object> child : children)
            {
                addCatalog(child, rows);
            }
        }
    }

    public ResultSet getTableTypes() throws SQLException
    {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        rows.add(Collections.<String, Object>singletonMap("TABLE_TYPE", "TABLE"));
        List<LabKeyResultSet.Column> columns = Collections.singletonList(new LabKeyResultSet.Column("TABLE_TYPE", String.class));
        return new LabKeyResultSet(rows, columns, _connection);
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException
    {
        _connection.setCatalog(catalog);
        validatePattern(schemaPattern, "schemaPattern");
        validatePattern(tableNamePattern, "tableNamePattern");
        if (columnNamePattern != null && !"%".equals(columnNamePattern))
        {
            throw new IllegalArgumentException("columnNamePattern must be null");
        }

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        GetQueryDetailsResponse response = getQueryDetails(schemaPattern, tableNamePattern);

        int ordinalPosition = 1;
        for (GetQueryDetailsResponse.Column column : response.getColumns())
        {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("TABLE_CAT", catalog);
            row.put("TABLE_SCHEM", response.getSchemaName());
            row.put("TABLE_NAME", response.getName());
            row.put("COLUMN_NAME", column.getName());
            row.put("DATA_TYPE", getSQLType(column.getType()));
            row.put("TYPE_NAME", column.getType());
            row.put("COLUMN_SIZE", -1);
            row.put("BUFFER_LENGTH", null);
            row.put("DECIMAL_DIGITS", null);
            row.put("NUM_PREC_RADIX", 10);
            row.put("NULLABLE", column.isNullable() ? columnNullable : columnNoNulls);
            row.put("REMARKS", column.getDescription());
            row.put("SQL_DATA_TYPE", null);
            row.put("SQL_DATETIME_SUB", null);
            row.put("CHAR_OCTET_LENGTH", null);
            row.put("ORDINAL_POSITION", ordinalPosition++);
            row.put("SCOPE_CATALOG", null);
            row.put("SCOPE_SCHEMA", null);
            row.put("SCOPE_TABLE", null);
            row.put("SOURCE_DATA_TYPE", null);
            row.put("IS_AUTOINCREMENT", column.isAutoIncrement() ? "YES" : "NO");
            row.put("IS_GENERATEDCOLUMN", column.isCalculated() || column.isAutoIncrement() || !column.isUserEditable() ? "YES" : "NO");
            rows.add(row);
        }

        List<LabKeyResultSet.Column> cols = new ArrayList<LabKeyResultSet.Column>();
        cols.add(new LabKeyResultSet.Column("TABLE_CAT", String.class));
        cols.add(new LabKeyResultSet.Column("TABLE_SCHEM", String.class));
        cols.add(new LabKeyResultSet.Column("TABLE_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("COLUMN_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("DATA_TYPE", Integer.class));
        cols.add(new LabKeyResultSet.Column("TYPE_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("COLUMN_SIZE", Integer.class));
        cols.add(new LabKeyResultSet.Column("BUFFER_LENGTH", Integer.class));
        cols.add(new LabKeyResultSet.Column("DECIMAL_DIGITS", Integer.class));
        cols.add(new LabKeyResultSet.Column("NUM_PREC_RADIX", Integer.class));
        cols.add(new LabKeyResultSet.Column("NULLABLE", Integer.class));
        cols.add(new LabKeyResultSet.Column("REMARKS", String.class));
        cols.add(new LabKeyResultSet.Column("SQL_DATA_TYPE", Integer.class));
        cols.add(new LabKeyResultSet.Column("SQL_DATETIME_SUB", Integer.class));
        cols.add(new LabKeyResultSet.Column("CHAR_OCTET_LENGTH", Integer.class));
        cols.add(new LabKeyResultSet.Column("ORDINAL_POSITION", Integer.class));
        cols.add(new LabKeyResultSet.Column("SCOPE_CATALOG", String.class));
        cols.add(new LabKeyResultSet.Column("SCOPE_SCHEMA", String.class));
        cols.add(new LabKeyResultSet.Column("SCOPE_TABLE", String.class));
        cols.add(new LabKeyResultSet.Column("SOURCE_DATA_TYPE", Short.class));
        cols.add(new LabKeyResultSet.Column("IS_AUTOINCREMENT", String.class));
        cols.add(new LabKeyResultSet.Column("IS_GENERATEDCOLUMN", String.class));

        return new LabKeyResultSet(rows, cols, _connection);
    }

    private void validatePattern(String pattern, String patternName)
    {
        if (pattern == null)
        {
            throw new IllegalArgumentException(patternName + " must request an exact match but was null");
        }
        if (pattern.contains("%") || pattern.contains("_"))
        {
            _log.info(patternName + " requested via a possible wildcard pattern, but interpreting as an exact match: " + pattern);
        }
    }

    private int getSQLType(String type)
    {
        if ("Integer".equalsIgnoreCase(type))
        {
            return Types.INTEGER;
        }
        if (type.contains("String"))
        {
            return Types.VARCHAR;
        }
        if ("Boolean".equalsIgnoreCase(type))
        {
            return Types.BOOLEAN;
        }
        if (type.contains("Date"))
        {
            return Types.DATE;
        }
        if (type.contains("Number"))
        {
            return Types.DOUBLE;
        }

        return Types.VARCHAR;
    }

    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    private GetQueryDetailsResponse getQueryDetails(String schema, String table) throws SQLException
    {
        String cacheKey = _connection.getFolderPath() + "." + schema + "." + table;
        GetQueryDetailsResponse results = _queryDetails.get(cacheKey);
        if (results == null)
        {
            GetQueryDetailsCommand command = new GetQueryDetailsCommand(schema, table);
            try
            {
                results = command.execute(_connection.getConnection(), _connection.getFolderPath());
            }
            catch (IOException e)
            {
                throw new SQLException(e);
            }
            catch (CommandException e)
            {
                // Ignore tables that aren't exposed directly for now
                throw new SQLException(e);
            }

            _queryDetails.put(cacheKey, results);
        }
        return results;
    }

    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException
    {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        GetQueryDetailsResponse response = getQueryDetails(schema, table);
        int keyIndex = 1;
        for (GetQueryDetailsResponse.Column column : response.getColumns())
        {
            if (column.isKeyField())
            {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("TABLE_CAT", catalog);
                row.put("TABLE_SCHEM", response.getSchemaName());
                row.put("TABLE_NAME", response.getName());
                row.put("COLUMN_NAME", column.getName());
                row.put("KEY_SEQ", keyIndex++);
                row.put("PK_NAME", null);
                rows.add(row);
            }
        }
        List<LabKeyResultSet.Column> cols = new ArrayList<LabKeyResultSet.Column>();
        cols.add(new LabKeyResultSet.Column("TABLE_CAT", String.class));
        cols.add(new LabKeyResultSet.Column("TABLE_SCHEM", String.class));
        cols.add(new LabKeyResultSet.Column("TABLE_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("COLUMN_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("KEY_SEQ", Short.class));
        cols.add(new LabKeyResultSet.Column("PK_NAME", String.class));

        return new LabKeyResultSet(rows, cols, _connection);
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException
    {
        GetQueryDetailsResponse response = getQueryDetails(schema, table);
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (GetQueryDetailsResponse.Column column : response.getColumns())
        {
            if (column.getLookup() != null)
            {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("PKTABLE_CAT", catalog);
                row.put("PKTABLE_SCHEM", column.getLookup().getSchemaName());
                row.put("PKTABLE_NAME", column.getLookup().getQueryName());
                row.put("PKCOLUMN_NAME", column.getLookup().getKeyColumn());
                row.put("FKTABLE_CAT", column.getLookup().getContainerPath() == null ? catalog : column.getLookup().getContainerPath());
                row.put("FKTABLE_SCHEM", response.getSchemaName());
                row.put("FKTABLE_NAME", response.getName());
                row.put("FKCOLUMN_NAME", column.getName());
                row.put("KEY_SEQ", 1);
                row.put("UPDATE_RULE", importedKeyNoAction);
                row.put("DELETE_RULE", importedKeyNoAction);
                row.put("FK_NAME", response.getName() + "_" + column.getName());
                row.put("PK_NAME", null);
                row.put("DEFERRABILITY", importedKeyNotDeferrable);
                rows.add(row);
            }
        }
        List<LabKeyResultSet.Column> cols = new ArrayList<LabKeyResultSet.Column>();
        cols.add(new LabKeyResultSet.Column("PKTABLE_CAT", String.class));
        cols.add(new LabKeyResultSet.Column("PKTABLE_SCHEM", String.class));
        cols.add(new LabKeyResultSet.Column("PKTABLE_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("PKCOLUMN_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("FKTABLE_CAT", String.class));
        cols.add(new LabKeyResultSet.Column("FKTABLE_SCHEM", String.class));
        cols.add(new LabKeyResultSet.Column("FKTABLE_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("FKCOLUMN_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("KEY_SEQ", Integer.class));
        cols.add(new LabKeyResultSet.Column("UPDATE_RULE", Short.class));
        cols.add(new LabKeyResultSet.Column("DELETE_RULE", Short.class));
        cols.add(new LabKeyResultSet.Column("FK_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("PK_NAME", String.class));
        cols.add(new LabKeyResultSet.Column("DEFERRABILITY", Short.class));

        return new LabKeyResultSet(rows, cols, _connection);
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getTypeInfo() throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public boolean supportsResultSetType(int type) throws SQLException
    {
        return true;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException
    {
        return true;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException
    {
        return true;
    }

    public boolean updatesAreDetected(int type) throws SQLException
    {
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException
    {
        return false;
    }

    public boolean insertsAreDetected(int type) throws SQLException
    {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException
    {
        return false;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public Connection getConnection() throws SQLException
    {
        return _connection;
    }

    public boolean supportsSavepoints() throws SQLException
    {
        return false;
    }

    public boolean supportsNamedParameters() throws SQLException
    {
        return false;
    }

    public boolean supportsMultipleOpenResults() throws SQLException
    {
        return true;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException
    {
        return true;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException
    {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT == holdability;
    }

    public int getResultSetHoldability() throws SQLException
    {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    public int getDatabaseMajorVersion() throws SQLException
    {
        return 0;
    }

    public int getDatabaseMinorVersion() throws SQLException
    {
        return 0;
    }

    public int getJDBCMajorVersion() throws SQLException
    {
        return 1;
    }

    public int getJDBCMinorVersion() throws SQLException
    {
        return 6;
    }

    public int getSQLStateType() throws SQLException
    {
        return sqlStateSQL;
    }

    public boolean locatorsUpdateCopy() throws SQLException
    {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException
    {
        return false;
    }

    public RowIdLifetime getRowIdLifetime() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public List<String> getSchemaNames() throws SQLException
    {
        try
        {
            GetSchemasCommand command = new GetSchemasCommand();
            _log.info("getSchemaNames:" + _connection.getFolderPath());
            GetSchemasResponse response = command.execute(_connection.getConnection(), _connection.getFolderPath());
            return response.getSchemaNames();
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

    public ResultSet getSchemas() throws SQLException
    {
        _log.log(Level.INFO, "getSchemas");
        return getSchemasResultSet(getSchemaNames());
    }

    private ResultSet getSchemasResultSet(List<String> schemaNames)
            throws SQLException
    {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (String schemaName : schemaNames)
        {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("TABLE_SCHEM", schemaName);
            row.put("TABLE_CATALOG", _connection.getCatalog());
            rows.add(row);
        }
        List<LabKeyResultSet.Column> columns = new ArrayList<LabKeyResultSet.Column>();
        columns.add(new LabKeyResultSet.Column("TABLE_SCHEM", String.class));
        columns.add(new LabKeyResultSet.Column("TABLE_CATALOG", String.class));
        return new LabKeyResultSet(rows, columns, _connection);
    }

    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
    {
        _connection.setCatalog(catalog);
        if (schemaPattern == null || schemaPattern.equals("%"))
        {
            return getSchemasResultSet(getSchemaNames());
        }
        if (schemaPattern.contains("%") || schemaPattern.contains("_"))
        {
            throw new IllegalArgumentException("schemaPattern must request an exact match, but was: " + schemaPattern);
        }
        _log.log(Level.INFO, "getSchemas: " + catalog + ", " + schemaPattern);
        List<String> selectedSchemaNames = new ArrayList<String>();
        for (String schemaName : getSchemaNames())
        {
            if (matches(schemaName, schemaPattern))
            {
                selectedSchemaNames.add(schemaName);
            }
        }
        return getSchemasResultSet(selectedSchemaNames);
    }

    private boolean matches(String name, String pattern)
    {
        return pattern == null || pattern.equalsIgnoreCase("%") || name.equalsIgnoreCase(pattern);
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
    {
        return false;
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException
    {
        return false;
    }

    public ResultSet getClientInfoProperties() throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException
    {
        return new LabKeyResultSet(Collections.<Map<String, Object>>emptyList(), Collections.<LabKeyResultSet.Column>emptyList(), _connection);
    }

    // These JDBC 4.1 methods must be "implemented" so JDK 7 can compile this class.

    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
