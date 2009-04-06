/*
 * Copyright (c) 2008-2009 LabKey Corporation
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

/**
 * Command for deleting rows from a read-write schema. The user associated
 * with the connection used when executing this command must have
 * permission to delete the data.
 * <p>
 * For details on schemas and queries, and example code, see the {@link SaveRowsCommand}.
 */
public class DeleteRowsCommand extends SaveRowsCommand
{
    /**
     * Constructs a DeleteRowsCommand for the given schemaName and queryName.
     * See the {@link SaveRowsCommand} for more details.
     * @param schemaName The schemaName
     * @param queryName The queryName.
     * @see SaveRowsCommand
     */
    public DeleteRowsCommand(String schemaName, String queryName)
    {
        super(schemaName, queryName, "deleteRows");
    }

    public DeleteRowsCommand(DeleteRowsCommand source)
    {
        super(source);
    }

    @Override
    public DeleteRowsCommand copy()
    {
        return new DeleteRowsCommand(this);
    }
}