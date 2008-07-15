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

import org.labkey.remoteapi.PostCommand;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.json.simple.JSONObject;
import org.apache.commons.codec.EncoderException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 5:21:23 PM
*/
public abstract class SaveRowsCommand extends PostCommand
{
    private String _schemaName;
    private String _queryName;
    private List<Map<String,Object>> _rows = new ArrayList<Map<String,Object>>();

    protected SaveRowsCommand(String schemaName, String queryName, String actionName)
    {
        super("query", actionName);
        assert null != schemaName;
        assert null != queryName;
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        _schemaName = schemaName;
    }

    public String getQueryName()
    {
        return _queryName;
    }

    public void setQueryName(String queryName)
    {
        _queryName = queryName;
    }

    public List<Map<String, Object>> getRows()
    {
        return _rows;
    }

    public void setRows(List<Map<String, Object>> rows)
    {
        _rows = rows;
    }

    public void addRow(Map<String,Object> row)
    {
        _rows.add(row);
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put("schemaName", getSchemaName());
        json.put("queryName", getQueryName());
        json.put("rows", null != getRows() ? getRows() : new ArrayList());
        return json;
    }

    public SaveRowsResponse execute(Connection connection, String folderPath) throws IOException, EncoderException
    {
        return (SaveRowsResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status)
    {
        return new SaveRowsResponse(text, status);
    }
}