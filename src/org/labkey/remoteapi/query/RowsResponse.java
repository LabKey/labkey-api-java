/*
 * Copyright (c) 2008-2016 LabKey Corporation
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

import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.HasRequiredVersion;
import org.labkey.remoteapi.collections.CaseInsensitiveHashMap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Base class for command responses that contain an array of rows
 * and meta-data about those rows. Primarily, this class converts
 * date values in the rows array to real Java Date objects.
 */
abstract class RowsResponse extends CommandResponse
{
    private final double _requiredVersion;

    /**
     * Constructs a new RowsResponse given the specified text and status code.
     * @param text The response text.
     * @param statusCode The HTTP status code.
     * @param contentType the Content-Type header value.
     * @param json The parsed JSONObject (or null if JSON was not returned.
     * @param hasRequiredVersion An object that implements HasRequiredVersion, such as the command that created this response
     */
    RowsResponse(String text, int statusCode, String contentType, JSONObject json, HasRequiredVersion hasRequiredVersion)
    {
        super(text, statusCode, contentType, json);
        fixupParsedData();
        caseInsensitizeRowMaps();
        _requiredVersion = hasRequiredVersion.getRequiredVersion();
    }

    /**
     * Returns the API version number required by the source command. This response returns data in a different format
     * depending on the required version
     * @return the requested API version number
     */
    public double getRequiredVersion()
    {
        return _requiredVersion;
    }

    /**
     * Returns the list of rows from the parsed response data. Note that numbers in the map values will be either of
     * type Double or type Long depending on the presence of a decimal point. The most reliable way to work with them
     * is to use the Number class. For example:
     * <pre><code>
     * for (Map&lt;String, Object&gt; row : response.getRows())
     * {
     *     Number key = (Number)row.get("Key");
     *     // use Number.intValue(), doubleValue(), longValue(), etc to get various primitive types
     * }
     * </code></pre>
     * @return The list of rows (each row is a Map), or null if the rows list was not included in the response.
     */
    public List<Map<String, Object>> getRows()
    {
        return getProperty("rows");
    }

    /**
     * Fixes up the parsed data. Currently, this converts string-based date literals into real Java Date objects.
     */
    private void fixupParsedData()
    {
        if (null == getParsedData())
            return;
        
        // Because JSON does not have a literal representation for dates we fixup date values in the response rows for
        // columns of type date. We also convert numeric values to their proper Java types based on the meta-data type
        // name (int vs float).

        //build up the list of date fields
        List<String> dateFields = new ArrayList<>();
        List<String> intFields = new ArrayList<>();
        List<String> floatFields = new ArrayList<>();
        List<Map<String, Object>> fields = getProperty("metaData.fields");
        if (null == fields)
            return;

        for (Map<String, Object> field : fields)
        {
            String type = (String)field.get("type");
            if ("date".equalsIgnoreCase(type))
                dateFields.add((String)field.get("name"));
            else if ("float".equalsIgnoreCase(type))
                floatFields.add((String)field.get("name"));
            else if ("int".equalsIgnoreCase(type))
                intFields.add((String)field.get("name"));
        }

        // If no fields to fixup, just return
        if (dateFields.size() == 0 && floatFields.size() == 0 && intFields.size() == 0)
            return;

        // If no rows, just return
        List<Map<String, Object>> rows = getRows();
        if (null == rows || rows.size() == 0)
            return;

        // The selectRows.api returns dates in a very particular format so that JavaScript can parse them into actual
        // date classes. If this format ever changes, we'll need to change the format string used here.
        // CONSIDER: use a library like ConvertUtils to avoid this dependency?
        DateParser dateFormat = new DateParser();
        boolean expandedFormat = getRequiredVersion() == 9.1;

        for (Map<String, Object> row : rows)
        {
            for (String field : dateFields)
            {
                //in expanded format, the value is a Map<String, Object> with several
                //possible properties, including "value" which is the column's value
                String valueFieldName = expandedFormat ? "value" : field;
                Map<String, Object> map = expandedFormat ? (Map<String, Object>)row.get(field) : row;
                Object dateString = map.get(valueFieldName);

                if (dateString instanceof String ds)
                {
                    //parse the string into a Java Date and reset the association
                    try
                    {
                        Date date = dateFormat.parse(ds);
                        if (null != date)
                        {
                            map.put(valueFieldName, date);
                        }
                    }
                    catch(ParseException e)
                    {
                        //just log it--if it doesn't parse, we can't fix it up
                        LogFactory.getLog(SelectRowsResponse.class).warn("Failed to parse date '"
                                + dateString + "': " + e);
                    }
                } //if the value is present and a string
            } //for each date field

            //floats
            for (String field : floatFields)
            {
                String valueFieldName = expandedFormat ? "value" : field;
                Map<String, Object> map = expandedFormat ? (Map<String, Object>)row.get(field) : row;
                Object value = map.get(valueFieldName);

                if (value instanceof Number num)
                {
                    map.put(valueFieldName, num.doubleValue());
                }
            }

            //ints
            for (String field : intFields)
            {
                String valueFieldName = expandedFormat ? "value" : field;
                Map<String, Object> map = expandedFormat ? (Map<String, Object>)row.get(field) : row;
                Object value = map.get(valueFieldName);

                if (value instanceof Number num)
                {
                    map.put(valueFieldName, num.intValue());
                }
            }
        } //for each row
    } //fixupParsedData()

    private void caseInsensitizeRowMaps()
    {
        //copy the row maps into case-insensitive hash maps
        List<Map<String, Object>> ciRows = new ArrayList<>();

        if (getRows() != null)
        {
            for (Map<String, Object> row : getRows())
            {
                //copy the row map into a case-insensitive hash map
                ciRows.add(new CaseInsensitiveHashMap<>(row));
            }
        }

        //reset the rows array
        getParsedData().put("rows", ciRows);
    }
}
