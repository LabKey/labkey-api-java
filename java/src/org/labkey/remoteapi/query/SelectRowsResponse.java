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

import org.labkey.remoteapi.CommandResponse;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 9:50:15 AM
*/

/**
 * Represents the response data from the selectRows.api command in particular.
 */
public class SelectRowsResponse extends RowsResponse
{
    public enum ColumnDataType
    {
        STRING,
        INT,
        FLOAT,
        BOOLEAN,
        DATE;

        public static ColumnDataType parseJsonType(String type)
        {
            if("string".equalsIgnoreCase(type))
                return STRING;
            else if("int".equalsIgnoreCase(type))
                return INT;
            else if("float".equalsIgnoreCase(type))
                return FLOAT;
            else if("boolean".equalsIgnoreCase(type))
                return BOOLEAN;
            else if("date".equalsIgnoreCase(type))
                return DATE;
            else
                return null;
        }
    }

    public SelectRowsResponse(String text, int statusCode)
    {
        super(text, statusCode);
    }

    public Number getRowCount()
    {
        return getProperty("rowCount");
    }

    public Map<String,Object> getMetaData()
    {
        return getProperty("metaData");
    }

    public Map<String,Object> getMetaData(String columnName)
    {
        assert null != columnName;
        List<Map<String,Object>> metaData = getProperty("metaData.fields");
        if(null == metaData)
            return null;
        for(Map<String,Object> entry : metaData)
        {
            if(columnName.equalsIgnoreCase((String)entry.get("name")))
                return entry;
        }
        return null;
    }

    public ColumnDataType getColumnDataType(String columnName)
    {
        Map<String,Object> meta = getMetaData(columnName);
        if(null == meta)
            return null;
        String type = (String)meta.get("type");
        return null == type ? null : ColumnDataType.parseJsonType(type);
    }

    public String getIdColumn()
    {
        return getProperty("metaData.id");
    }

    public List<Map<String,Object>> getColumnModel()
    {
        return getProperty("columnModel");
    }

    public Map<String,Object> getColumnModel(String columnName)
    {
        assert null != columnName;
        List<Map<String,Object>> colmodel = getColumnModel();
        if(null == colmodel)
            return null;

        for(Map<String,Object> col : colmodel)
        {
            if(columnName.equalsIgnoreCase((String)col.get("dataIndex")))
                return col;
        }

        return null;
    }

}