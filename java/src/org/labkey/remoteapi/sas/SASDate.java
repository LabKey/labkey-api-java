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

import java.util.Date;

/**
 * User: adam
 * Date: Feb 3, 2009
 * Time: 3:33:25 PM
 */
public class SASDate
{
    // TODO: Adjust to GMT?   d.setTime(d.getTime()-TimeZone.getDefault().getRawOffset());

    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    // Java dates are based on days since 1/1/1970; SAS Dates are based on days since 1/1/1960
    private static final double DAYS_BETWEEN_19700101_AND_19600101 = 3653;

    static Date convertToJavaDate(double sasDate)
    {
        double javaDays = sasDate - DAYS_BETWEEN_19700101_AND_19600101;
        long javaTime = Math.round(javaDays) * MILLISECONDS_PER_DAY;
        return new Date(javaTime);
    }

    static double convertToSASDate(Date javaDate)
    {
        double javaDays = javaDate.getTime() / MILLISECONDS_PER_DAY;
        return javaDays + DAYS_BETWEEN_19700101_AND_19600101;
    }
}
