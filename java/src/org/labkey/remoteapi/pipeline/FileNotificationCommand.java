/*
 * Copyright (c) 2011 LabKey Corporation
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
package org.labkey.remoteapi.pipeline;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

/**
 * Notifies the server that files may have become available for the pipeline to process. Causes the server to
 * look through all of the jobs that are blocked, waiting for files, and release the ones whose files are now
 * available.
 * User: jeckels
 * Date: Feb 15, 2011
 */
public class FileNotificationCommand extends Command<FileNotificationResponse>
{
    public FileNotificationCommand()
    {
        super("pipeline-analysis", "fileNotification");
    }

    @Override
    protected FileNotificationResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new FileNotificationResponse(text, status, contentType, json, this);
    }
}
