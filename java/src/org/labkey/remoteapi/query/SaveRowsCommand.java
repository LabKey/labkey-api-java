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
import org.labkey.remoteapi.CommandException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 5:21:23 PM
*/

/**
 * Base class for commands that make changes to rows exposed from a given
 * query in a given schema. Clients should use {@link UpdateRowsCommand},
 * {@link InsertRowsCommand} or {@link DeleteRowsCommand} and not this class directly.
 * <p>
 * All three of these sub-classes post similar JSON to the server, so this class
 * does all the common work. The client must supply three things: the schemaName,
 * the queryName and an array of 'rows' (i.e. Maps). The rows are added via
 * the {@link #addRow(Map)} or {@link #setRows(List)} methods.
 * <p>
 * All data exposed from the LabKey Server is organized into a set of queries
 * contained in a set of schemas. A schema is simply a group of queries, identified
 * by a name (e.g., 'lists' or 'study'). A query is particular table or view within
 * that schema (e.g., 'People' or 'Peptides'). Currently, clients may update rows in
 * base tables only, and not in joined views. Therefore the query name must be the
 * name of a table in the schema.
 * <p>
 * To view the schemas and queries exposed in a given folder, add a Query web part
 * to your portal page and choose the option "Show the list of tables in this schema"
 * in the part configuration page. Alternatively, if it is exposed, click on the Query
 * tab across the top of the main part of the page.
 * <p>
 * Examples:
 * <code><pre>
 *  Connection cn = new Connection("http://localhost:8080", user, password);
 *
 *  //Insert Rows Command
 *  InsertRowsCommand cmd = new InsertRowsCommand("lists", "People");
 *
 *  Map&lt;String,Object&gt; row = new HashMap&lt;String,Object&gt;();
 *  row.put("FirstName", "Insert");
 *  row.put("LastName", "Test");
 *
 *  cmd.addRow(row); //can add multiple rows to insert many at once
 *  SaveRowsResponse resp = cmd.execute(cn, "Api Test");
 *
 *  //get the newly-assigned primary key value from the first return row
 *  int newKey = resp.getRows().get(0).get("Key");
 *
 *  //Update Rows Command
 *  UpdateRowsCommand cmdUpd = new UpdateRowsCommand("lists", "People");
 *  row = new HashMap&lt;String,Object&gt;();
 *  row.put("Key", newKey);
 *  row.put("LastName", "Test UPDATED");
 *  cmdUpd.addRow(row);
 *  resp = cmdUpd.execute(cn, "Api Test");
 *
 *  //Delete Rows Command
 *  DeleteRowsCommand cmdDel = new DeleteRowsCommand("lists", "People");
 *  row = new HashMap&lt;String,Object&gt;();
 *  row.put("Key", newKey);
 *  cmdDel.addRow(row);
 *  resp = cmdDel.execute(cn, "Api Test");
 * </pre></code>
 */
public abstract class SaveRowsCommand extends PostCommand
{
    private String _schemaName;
    private String _queryName;
    private List<Map<String,Object>> _rows = new ArrayList<Map<String,Object>>();

    /**
     * Constructs a new SaveRowsCommand for a given schema, query and action name.
     * @param schemaName The schema name.
     * @param queryName The query name.
     * @param actionName The action name to call (supplied by the derived class).
     */
    protected SaveRowsCommand(String schemaName, String queryName, String actionName)
    {
        super("query", actionName);
        assert null != schemaName;
        assert null != queryName;
        _schemaName = schemaName;
        _queryName = queryName;
    }

    /**
     * Returns the schema name.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Sets the schema name
     * @param schemaName The new schema name.
     */
    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    /**
     * Returns the query name
     * @return the query name.
     */
    public String getQueryName()
    {
        return _queryName;
    }

    /**
     * Sets a new query name to update
     * @param queryName the query name.
     */
    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    /**
     * Returns the current list of 'rows' (i.e., Maps) that will
     * be sent to the server.
     * @return The list of rows.
     */
    public List<Map<String, Object>> getRows()
    {
        return _rows;
    }

    /**
     * Sets the list of 'rows' (i.e., Maps) to be sent to the server.
     * @param rows The rows to send
     */
    public void setRows(List<Map<String, Object>> rows)
    {
        _rows = rows;
    }

    /**
     * Adds a row to the list of rows to be sent to the server.
     * @param row The row to add
     */
    public void addRow(Map<String,Object> row)
    {
        _rows.add(row);
    }

    /**
     * Dynamically builds the JSON object to send based on the current
     * schema name, query name and rows list.
     * @return The JSON object to send.
     */
    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("queryName", getQueryName());
        json.put("rows", null != getRows() ? getRows() : new ArrayList());
        return json;
    }

    public SaveRowsResponse execute(Connection connection, String folderPath) throws IOException, CommandException
    {
        return (SaveRowsResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveRowsResponse(text, status, contentType, json);
    }
}