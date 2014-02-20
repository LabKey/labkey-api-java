/*
 * Copyright (c) 2013-2014 LabKey Corporation
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
package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class RunTransformResponse extends BaseTransformResponse
{
    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text          The response text
     * @param statusCode    The HTTP status code
     * @param contentType   The response content type
     * @param json          The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public RunTransformResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     *
     * @return The pipeline job id.
     */
    public String getJobId()
    {
        return getProperty("jobId");
    }

    /**
     *
     * @return The URL for the pipeline status page for this job
     */
    public String getPipelineURL()
    {
        return getProperty("pipelineURL");
    }

    /**
     *
     * @return The job status- success, complete, error, or 'no work'
     */
    public String getStatus()
    {
        return getProperty("status");
    }
}
