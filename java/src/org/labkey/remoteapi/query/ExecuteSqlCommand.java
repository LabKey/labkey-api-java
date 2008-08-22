/*
 * Copyright (c) 2008 LabKey Corporation
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

import org.apache.commons.codec.EncoderException;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;

import java.io.IOException;

/*
* User: Dave
* Date: Jul 22, 2008
* Time: 1:32:57 PM
*/

/**
 * Command for executing arbitrary LabKey SQL.
 * <p>
 * LabKey SQL is variant of standard SQL that supports most of the SELECT-related
 * operations. For more information on LabKey SQL, see the following page:
 * <p>
 * <a href="https://www.labkey.org/wiki/home/Documentation/page.view?name=labkeySql">
 * https://www.labkey.org/wiki/home/Documentation/page.view?name=labkeySql</a>
 * <p>
 * The response of this command is exactly the same as the
 * {@link org.labkey.remoteapi.query.SelectRowsCommand}, so the response object
 * will be of type {@link org.labkey.remoteapi.query.SelectRowsResponse}.
 */
public class ExecuteSqlCommand extends PostCommand
{
    private String _schemaName;
    private String _sql;
    private int _maxRows = -1;
    private int _offset = 0;

    /**
     * Constructs an ExceuteSqlCommand, initialized with a schema name.
     * <p>
     * When using this constructor, you must call the {@link #setSql(String)}
     * method before executing the command.
     * @param schemaName The schema name to query.
     */
    public ExecuteSqlCommand(String schemaName)
    {
        super("query", "executeSql");
        _schemaName = schemaName;
    }

    /**
     * Constructs an ExecuteSqlCommand, initialized with a schema name and SQL query.
     * @param schemaName The schema name ot query.
     * @param sql The SQL query.
     */
    public ExecuteSqlCommand(String schemaName, String sql)
    {
        super("query", "executeSql");
        _schemaName = schemaName;
        _sql = sql;
    }

    /**
     * Returns the current schema name.
     * @return The current schema name.
     */
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
     * Returns the current row limit value. Defaults to -1, meaning return all rows.
     * @return The current row limit value.
     */
    public int getMaxRows()
    {
        return _maxRows;
    }

    /**
     * Sets the current row limit value. If this is set to a positive value, only
     * the first <code>maxRows</code> rows will be returned from the server.
     * @param maxRows The maximim number of rows to return, or -1 to get all rows (default).
     */
    public void setMaxRows(int maxRows)
    {
        _maxRows = maxRows;
    }

    /**
     * Returns the index of the first row in the resultset to return (defaults to 0).
     * @return The current offset index.
     */
    public int getOffset()
    {
        return _offset;
    }

    /**
     * Sets the index of the first row in the resultset to return from the server.
     * Use this in conjunction with {@link #setMaxRows(int)} to return pages of
     * rows at a time from the server.
     * @param offset The current offset index.
     */
    public void setOffset(int offset)
    {
        _offset = offset;
    }

    public SelectRowsResponse execute(Connection connection, String folderPath) throws IOException, EncoderException
    {
        assert null != _schemaName : "You must set the schemaName before executing!";
        assert null != _sql : "You must set the Sql before executing!";
        return (SelectRowsResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status, String contentType)
    {
        return new SelectRowsResponse(text, status, contentType);
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("sql", getSql());
        if(getMaxRows() >= 0)
            json.put("maxRows", getMaxRows());
        if(getOffset() > 0)
            json.put("offset", getOffset());
        return json;
    }
}