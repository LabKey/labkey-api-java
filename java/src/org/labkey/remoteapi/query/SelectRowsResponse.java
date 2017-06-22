/*
 * Copyright (c) 2008-2017 LabKey Corporation
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
import org.labkey.remoteapi.Command;

import java.util.List;
import java.util.Map;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 9:50:15 AM
*/

/**
 * The command response class returned from the
 * {@link SelectRowsCommand#execute(org.labkey.remoteapi.Connection, String)}
 * method. This class provides helpful methods for obtaining specific bits
 * of the parsed response data.
 * @see SelectRowsCommand
 */
public class SelectRowsResponse extends RowsResponse
{
    /**
     * An enumeration of the possible column data types
     */
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

    /**
     * Constructs a new SelectRowsResponse given the response text and HTTP status code.
     * @param text The response text.
     * @param statusCode The HTTP status code.
     * @param contentType The Content-Type header value.
     * @param json The parsed JSONObject (or null if no JSON was returned
     * @param sourceCommand A copy of the command that created this response
     */
    public SelectRowsResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     * Returns the number of rows this query could return. If a maximum row limit was set
     * on the SelectRowsCommand, this value may be higher than the actual number of rows
     * returned. This value allows clients that page results to know the total number of
     * possible rows, even if only a page of them was returned.
     * @return The total number of rows, or null if this value was not returned by the server.
     */
    public Number getRowCount()
    {
        return getProperty("rowCount");
    }

    /**
     * Returns an iterable Rowset. Use this to iterate over the rows,
     * working with the Row interface, which hides the differences between
     * the normal (&lt;9.1) and extended (&gt;=9.1) response formats.
     * @return An iterable Rowset.
     */
    public Rowset getRowset()
    {
        return new RowsResponseRowset((List<Map<String,Object>>)getProperty("rows"));
    }

    /**
     * Returns the meta-data section of the response. This map contains the following
     * entries:
     * <ul>
     *  <li>id = the column name that contains the primary key for the row.</li>
     *  <li>fields = a List of Maps, one for each result column. Each map contains the following properties:
     *  <ul>
     *      <li>name = the name of the column.</li>
     *      <li>type = the JSON type name of the column ('string', 'boolean', 'date', 'int', or 'float').</li>
     *  </ul></li>
     * </ul>
     * @return The meta-data section of the response, or null if no section was returned by the server.
     */
    public Map<String,Object> getMetaData()
    {
        return getProperty("metaData");
    }

    /**
     * Returns the meta-data for a given column name. See {@link #getMetaData()} for more
     * information on the contents of this map.
     * @param columnName The requested column name.
     * @return The meta-data for that column or null if that column was not found.
     */
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

    /**
     * Returns the data type for the requested column name.
     * @param columnName The column name.
     * @return The column's data type, or null if the column was not found.
     */
    public ColumnDataType getColumnDataType(String columnName)
    {
        Map<String,Object> meta = getMetaData(columnName);
        if(null == meta)
            return null;
        String type = (String)meta.get("type");
        return null == type ? null : ColumnDataType.parseJsonType(type);
    }

    /**
     * Returns the name of the column containing the primary key value for each row.
     * @return The name of the primary key column, or null if that information
     * was not returned by the server.
     */
    public String getIdColumn()
    {
        return getProperty("metaData.id");
    }

    /**
     * Returns the column model section of the response. The column model contains
     * user-interface-related information about the column, such as its header caption,
     * whether it is editable or required, etc.
     * <p>
     * This method will return a List of Maps, one for each column in the resultset. Each
     * Map will contain the following properties:
     * <ul>
     *  <li>hidden = whether this column should be hidden (Boolean).</li>
     *  <li>sortable = whether this column can be used for sorting (Boolean).</li>
     *  <li>align = the horizontal alignment for this column (String, 'left'|'center'|'right').</li>
     *  <li>width = the default width (in CSS width units) for this column (String).</li>
     *  <li>dataIndex = the name of the column (String).</li>
     *  <li>required = whether this column is required (Boolean).</li>
     *  <li>editable = whether this column should be editable (Boolean).</li>
     *  <li>header = the header caption for this column (String).</li>
     * </ul> 
     * @return The column model, or null if none was returned by the server.
     */
    public List<Map<String,Object>> getColumnModel()
    {
        return getProperty("columnModel");
    }

    /**
     * Returns the column properties for the specified column name.
     * See {@link #getColumnModel()} for more information.
     * @param columnName The column name.
     * @return The properties for the specified column, or null if the column was not found.
     */
    public Map<String,Object> getColumnModel(String columnName)
    {
        return findObject(getColumnModel(), "dataIndex", columnName);
    }

}