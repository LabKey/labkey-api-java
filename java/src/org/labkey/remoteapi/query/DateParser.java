/*
 * Copyright (c) 2012-2017 LabKey Corporation
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: matthewb
 * Date: 2012-03-12
 * Time: 10:55 AM
 *
 * not static because SimpleDatFormat is not thread-safe
 */
public class DateParser
{
    SimpleDateFormat[] formats = new SimpleDateFormat[]
    {
        new SimpleDateFormat("yyyy/MM/d HH:mm:ss"),
        new SimpleDateFormat("yyyy-MM-d HH:mm:ss"),
        new SimpleDateFormat("d MMM yyyy HH:mm:ss")
    };

    public DateParser()
    {
        for (SimpleDateFormat f : formats)
            f.setLenient(true);
    }

    public Date parse(String s) throws ParseException
    {
        ParseException ex = null;
        for (SimpleDateFormat f : formats)
        {
            try
            {
                return f.parse(s);
            }
            catch (ParseException x)
            {
                ex = x;
            }
        }
        if (null != ex)
            throw ex;
        return null;
    }
}
