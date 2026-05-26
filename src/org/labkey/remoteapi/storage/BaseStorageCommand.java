/*
 * Copyright (c) 2022-2026 LabKey Corporation
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
package org.labkey.remoteapi.storage;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

public abstract class BaseStorageCommand extends PostCommand<StorageCommandResponse>
{
    private final StorageRow _storageRow;

    public BaseStorageCommand(String action, StorageRow storageRow)
    {
        super("storage", action);
        _storageRow = storageRow;
    }

    @Override
    public double getRequiredVersion()
    {
        return -1;
    }

    @Override
    protected StorageCommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new StorageCommandResponse(text, status, contentType, json);
    }

    /**
     * Dynamically builds the JSON object to send based on the storageRow.
     * @return The JSON object to send.
     */
    @Override
    public JSONObject getJsonObject()
    {
        return _storageRow.toJsonObject();
    }
}
