/*
 * Copyright (c) 2008-2018 LabKey Corporation
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
package org.labkey.remoteapi.query;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Command for executing arbitrary LabKey SQL.
 * <p>
 * LabKey SQL is variant of standard SQL that supports most of the SELECT-related
 * operations. For more information on LabKey SQL, see the following page:
 * <p>
 * <a href="https://www.labkey.org/Documentation/wiki-page.view?name=labkeySql">
 * https://www.labkey.org/Documentation/wiki-page.view?name=labkeySql</a>
 * <p>
 *
 * Uses stream-lined sql-execute.api instead of query-executesql.api
 */
public class SqlExecuteCommand extends PostCommand<CommandResponse>
{
    private static final char nul_char = '\u0000';
    private static final char bs_char  = '\u0008'; // backspace
    private static final char rs_char  = '\u001E'; // record separator
    private static final char us_char  = '\u001F';  // unit separator

    private String _schemaName;
    private String _sql;
    private Map<String, Object> _queryParameters = new HashMap<>();
    private String _sep = us_char + "\t";
    private String _eol = us_char + "\n";
    private boolean _compact = true;

    /**
     * Constructs an ExecuteSqlCommand, initialized with a schema name.
     * <p>
     * When using this constructor, you must call the {@link #setSql(String)}
     * method before executing the command.
     * @param schemaName The schema name to query.
     */
    public SqlExecuteCommand(String schemaName)
    {
        this(schemaName, null);
    }

    public SqlExecuteCommand(SqlExecuteCommand source)
    {
        super(source);
        _schemaName = source._schemaName;
        _sql = source._sql;
        _queryParameters.putAll(source._queryParameters);
        _sep = source._sep;
        _eol= source._eol;
        _compact = source._compact;
    }

    /**
     * Constructs an ExecuteSqlCommand, initialized with a schema name and SQL query.
     * @param schemaName The schema name ot query.
     * @param sql The SQL query.
     */
    public SqlExecuteCommand(String schemaName, String sql)
    {
        super("sql", "execute");
        _schemaName = schemaName;
        _sql = sql;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Sets the current schema name.
     * @param schemaName The new schema name to query.
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    /**
     * Returns the current SQL query.
     * @return The current SQL query.
     */
    public String getSql()
    {
        return _sql;
    }

    /**
     * Sets the SQL query to execute.
     * @param sql The new SQL query.
     */
    public void setSql(String sql)
    {
        _sql = sql;
    }

    /**
     Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     @return the set of query parameters for the SQL references
     */
    public Map<String, Object> getQueryParameters()
    {
        return _queryParameters;
    }

    /**
     Map of name (string)/value pairs for the values of parameters if the SQL references underlying queries
     that are parameterized.
     @param parameters a map of the named parameters to use in the underlying parameterized queries
     */
    public void setQueryParameters(Map<String, String> parameters)
    {
        _queryParameters.clear();
        _queryParameters.putAll(parameters);
    }

    public String getLineSeparator()
    {
        return _eol;
    }
    public String getFieldSeparator()
    {
        return _sep;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schema", getSchemaName());
        json.put("sql", getSql());
        json.put("parameters", getQueryParameters());
        json.put("eol", _eol);
        json.put("sep", _sep);
        json.put("compact", _compact);
        return json;
    }
}