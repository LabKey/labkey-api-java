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

import org.labkey.remoteapi.PostCommand;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 5:20:04 PM
*/
public class DeleteRowsCommand extends SaveRowsCommand
{
    public DeleteRowsCommand(String schemaName, String queryName)
    {
        super(schemaName, queryName, "deleteRows");
    }
}