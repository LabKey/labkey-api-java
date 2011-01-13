/*
 * Copyright (c) 2008-2011 LabKey Corporation
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
* User: Dave
* Date: Jul 11, 2008
* Time: 5:24:45 PM
*/

/**
 * Command for updating rows into a table in a read-write schema.
 * The user associated with the connection used when executing this
 * command must have permission to update data into the specified query.
 * <p>
 * For details on schemas and queries, and example code, see the {@link SaveRowsCommand}.
 * @see SaveRowsCommand
 */
public class UpdateRowsCommand extends SaveRowsCommand
{
    /**
     * Constructs an UpdateRowsCommand for the given schemaName and queryName.
     * See the {@link SaveRowsCommand} for more details.
     * @param schemaName The schemaName
     * @param queryName The queryName.
     * @see SaveRowsCommand
     */
    public UpdateRowsCommand(String schemaName, String queryName)
    {
        super(schemaName, queryName, "updateRows");
    }

    public UpdateRowsCommand(UpdateRowsCommand source)
    {
        super(source);
    }

    @Override
    public UpdateRowsCommand copy()
    {
        return new UpdateRowsCommand(this);
    }
}