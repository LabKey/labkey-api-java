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

import org.apache.commons.logging.LogFactory;
import org.labkey.remoteapi.CommandResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 11:57:17 AM
*/
public abstract class RowsResponse extends CommandResponse
{
    protected RowsResponse(String text, int statusCode)
    {
        super(text, statusCode);
        fixupParsedData();
    }

    public List<Map<String,Object>> getRows()
    {
        return getProperty("rows");
    }

    protected void fixupParsedData()
    {
        //because JSON does not have a literal representation for dates
        //we need to fixup date values in the response rows for columns
        //of type date.

        //build up the list of date fields
        List<String> dateFields = new ArrayList<String>();
        List<Map<String,Object>> fields = getProperty("metaData.fields");
        for(Map<String,Object> field : fields)
        {
            String type = (String)field.get("type");
            if("date".equalsIgnoreCase(type))
                dateFields.add((String)field.get("name"));
        }

        if(dateFields.size() == 0)
            return;

        //run the rows array and fixup date fields
        List<Map<String,Object>> rows = getRows();
        if(null == rows || rows.size() == 0)
            return;

        //The selectRows.api returns dates in a very particular format so that
        //JavaScript can parse them into actual date classes. If this format ever
        //changes, we'll need to change the format string used here.
        //CONSIDER: use a library like ConvertUtils to avoid this dependency?
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss Z");
        for(Map<String,Object> row : rows)
        {
            for(String field : dateFields)
            {
                Object dateString = row.get(field);
                if(null != dateString && dateString instanceof String)
                {
                    //parse the string into a Java Date and
                    //reset the association
                    try
                    {
                        Date date = dateFormat.parse((String)dateString);
                        if(null != date)
                            row.put(field, date);
                    }
                    catch(ParseException e)
                    {
                        //just log it--if it doesn't parse, we can't fix it up
                        LogFactory.getLog(SelectRowsResponse.class).warn("Failed to parse date '"
                                + dateString + "': " + e);
                    }
                } //if the value is present and a string
            } //for each date field
        } //for each row
    } //fixupParsedData()

}