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
package org.labkey.remoteapi.query;

/*
* User: dave
* Date: Oct 7, 2009
* Time: 11:40:28 AM
*/

/**
 * Provides a format-neutral way of obtaining a row's value. If you requested the extended format
 * (see SelectRowsCommand.setExtendedFormat()), you may also get the display value (if different),
 * the url, the missing value indicator, and the missing value raw value. Note that all of these
 * may return null if the column does not have a url, is not missing-value enabled, or does not
 * have a different display value from its raw value. 
 */
public interface Row
{
    /**
     * Returns a column's raw value.
     * @param columnName The column name.
     * @return The column's raw value or null if the column was not found.
     */
    public Object getValue(String columnName);

    /**
     * Returns a column's display value (if different from value).
     * @param columnName The column name.
     * @return The display value, or null if the column was not found or does not have a different display value.
     */
    public Object getDisplayValue(String columnName);

    /**
     * Returns a column's URL
     * @param columnName The column name.
     * @return The column's URL value, or null if the column was not found or does not have a URL.
     */
    public String getUrl(String columnName);

    /**
     * Returns a column's missing-value indicator.
     * @param columName The column name.
     * @return The column's mising-value indicator, or null if the column was not found or is not missing-value enabled.
     */
    public String getMvValue(String columName);

    /**
     * Returns the column's missing-value raw value. This is the actual value imported into the database along
     * with the missing-value indicator.
     * @param columnName The column name.
     * @return The column's mising-value raw value, or null if the column was not found or is not missing-value enabled.
     */
    public Object getMvRawValue(String columnName);
}
