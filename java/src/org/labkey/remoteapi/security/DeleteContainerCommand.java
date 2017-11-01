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
package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

/**
 * Delete a container (project/folder/workbook) and all of its children. Takes no configuration, just a target container
 * path when executing the command.
 * User: jeckels
 * Date: 9/11/12
 */
public class DeleteContainerCommand extends PostCommand<DeleteContainerResponse>
{
    public DeleteContainerCommand()
    {
        super("core", "deleteContainer");
    }

    public DeleteContainerCommand(DeleteContainerCommand source)
    {
        super(source);
    }

    @Override
    protected DeleteContainerResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new DeleteContainerResponse(text, status, contentType, json, this);
    }

    @Override
    public DeleteContainerCommand copy()
    {
        return new DeleteContainerCommand(this);
    }

}
