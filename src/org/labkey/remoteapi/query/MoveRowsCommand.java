/*
 * Copyright (c) 2023 LabKey Corporation
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

import org.json.JSONObject;

/**
 * Command for moving rows from a compatible schema table. The user associated
 * with the connection used when executing this command must have
 * permission to update data for the source container and insert data
 * for the target container.
 */
public class MoveRowsCommand extends SaveRowsCommand
{
    private final String _targetContainerPath;

    /**
     * Constructs a MoveRowsCommand for the given targetContainerPath, schemaName, and queryName.
     * See the {@link SaveRowsCommand} for more details.
     * @param targetContainerPath The targetContainerPath
     * @param schemaName The schemaName
     * @param queryName The queryName.
     * @see SaveRowsCommand
     */
    public MoveRowsCommand(String targetContainerPath, String schemaName, String queryName)
    {
        super(schemaName, queryName, "moveRows");
        _targetContainerPath = targetContainerPath;
    }

    @Override
    public JSONObject getJsonObject()
    {
        final JSONObject jsonObject = super.getJsonObject();
        jsonObject.put("targetContainerPath", _targetContainerPath);
        return jsonObject;
    }
}
