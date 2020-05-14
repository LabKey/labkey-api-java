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
* Time: 11:41:11 AM
*/

/**
 * Provides an iterable interface over the response rows that hides
 * the differences between the simple and extended response formats.
 * You may obtain one of these objects from SelectRowsResponse.getRowset()
 * and may use it in a standard for-each loop, like so:
 * <code>
 * SelectRowsResponse resp = myCommand.execute(...);
 * for (Row row : resp.getRowset())
 * {
 *      Object value = row.getValue("MyColumn");
 * }
 * </code>
 */
public interface Rowset extends Iterable<Row>
{
    public int getSize();
    
}
