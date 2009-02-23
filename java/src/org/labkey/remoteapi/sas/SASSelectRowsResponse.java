/*
 * Copyright (c) 2009 LabKey Corporation
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
package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: adam
 * Date: Jan 10, 2009
 * Time: 11:09:53 PM
 */
public class SASSelectRowsResponse
{
    private final SelectRowsResponse _resp;
    private Iterator<Map<String, Object>> _rowIterator;
    private Map<String, Object> _currentRow;

    private static final Stash<SelectRowsResponse> _stash = new Stash<SelectRowsResponse>(60000);  // Stash entries for up to 60 seconds

    // We need one constructor per command class because of SAS's method calling limitations (object parameters must match expected class exactly).
    public SASSelectRowsResponse(SASConnection cn, SASSelectRowsCommand command, String folderPath) throws CommandException, IOException
    {
        _resp = command.execute(cn, folderPath);
    }

    public SASSelectRowsResponse(SASConnection cn, SASExecuteSqlCommand command, String folderPath) throws CommandException, IOException
    {
        _resp = command.execute(cn, folderPath);
    }

    public SASSelectRowsResponse(String key)
    {
        _resp = _stash.get(key);
        _rowIterator = _resp.getRows().iterator();
    }

    public int getColumnCount()
    {
        List<Map> fields = (List<Map>)_resp.getMetaData().get("fields");
        return fields.size();
    }

    public String getColumnName(double index)
    {
        int i = (int)Math.round(index);
        List<Map<String, String>> fields = (List<Map<String, String>>)_resp.getMetaData().get("fields");
        return fields.get(i).get("name");
    }

    public String getType(String columnName)
    {
        SelectRowsResponse.ColumnDataType type = _resp.getColumnDataType(columnName);
        return type.toString();
    }

    public boolean isHidden(double index)
    {
        int i = (int)Math.round(index);
        Map<String,Object> columnModel = _resp.getColumnModel().get(i);
        return (Boolean)columnModel.get("hidden");
    }

    public boolean hasQC(double index)
    {
        int i = (int)Math.round(index);
        Map<String,Object> columnModel = _resp.getColumnModel().get(i);
        return (Boolean)columnModel.get("hidden");         // TODO: retrieve QC indicator
    }

    public String stash()
    {
        return _stash.put(_resp);
    }

    public boolean getRow()
    {
        boolean hasNext = _rowIterator.hasNext();

        if (hasNext)
            _currentRow = _rowIterator.next();

        return hasNext;
    }


    /*  TODO: Move the remaining methods to SASRow and return (new up) a SASRow instead of using getRow() */

    // Detect and handle both LabKey 8.3 and 9.1 formats
    private Object getValue(String key)
    {
        Object o = _currentRow.get(key);

        if (o instanceof JSONObject)
            return ((JSONObject)o).get("value");
        else
            return o;
    }

    public boolean isNull(String key)
    {
        return null == getValue(key);
    }

    public String getCharacter(String key)
    {
        return (String)getValue(key);
    }

    public double getNumeric(String key)
    {
        return ((Number)getValue(key)).doubleValue();
    }

    public boolean getBoolean(String key)
    {
        return (Boolean)getValue(key);
    }

    public double getDate(String key)
    {
        // TODO: Remove this temp hack once 9.1 format handles dates correctly
        Object value = getValue(key);
        Date d = value instanceof Date ? (Date)value : new Date((String)value);
        return SASDate.convertToSASDate(d);
    }

    public String getQCValue(String key)
    {
        Object o = _currentRow.get(key);

        if (!(o instanceof JSONObject))
            throw new IllegalStateException("QC values are only available from LabKey 9.1 or later servers");

        return (String)((JSONObject)o).get("qcValue");
    }
}
