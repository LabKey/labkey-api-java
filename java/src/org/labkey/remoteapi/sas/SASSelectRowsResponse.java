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

    // We need one constructor per command class because of SAS's method-calling limitations (object parameters must match expected class exactly).
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

    private Object getColumnModelProperty(double index, String propName)
    {
        int i = (int)Math.round(index);
        Map<String, Object> columnModel = _resp.getColumnModel().get(i);
        return columnModel.get(propName);
    }

    public boolean isHidden(double index)
    {
        return (Boolean)getColumnModelProperty(index, "hidden");
    }

    public int getScale(double index)
    {
        int scale = ((Long)getColumnModelProperty(index, "scale")).intValue();
        return 0 == scale ? 100 : scale;  // TODO: Temp hack -- 8.3 servers return different scale values than 9.1 -- for strings, scale == 0 at times
    }

    public boolean allowsMissingValues(String columnName)
    {
        Boolean allowsQC = (Boolean)_resp.getMetaData(columnName).get("allowsQC");

        return (null != allowsQC && allowsQC);
    }

    public String getMissingValuesCode()
    {
        Map<String, String> qcInfo = (Map<String, String>)_resp.getParsedData().get("qcInfo");

        StringBuilder values = new StringBuilder();
        StringBuilder footnotes = new StringBuilder();

        if (null != qcInfo)
        {
            int count = 1;

            for (Map.Entry<String, String> missing : qcInfo.entrySet())
            {
                values.append(" ").append(missing.getKey());

                if (count <= 10)
                    footnotes.append("footnote").append(count++).append(" \"").append(missing.getKey()).append(" - ").append(missing.getValue()).append("\";\n");
            }
        }

        if (values.length() > 0)
            return "missing" + values + ";\n" + footnotes;
        else
            return "footnote;";
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
        Date d = (Date)getValue(key);
        return SASDate.convertToSASDate(d);
    }

    public String getMissingValue(String key)
    {
        Object o = _currentRow.get(key);

        if (!(o instanceof JSONObject))
            throw new IllegalStateException("Missing values are only available when requiring LabKey 9.1 or later version");

        return (String)((JSONObject)o).get("qcValue");
    }
}
