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

import java.util.Date;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class UpdateTransformConfigurationResponse extends BaseTransformResponse
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
    public UpdateTransformConfigurationResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    public JSONObject getResult()
    {
        return getProperty("result");
    }

    /**
     *
     * @return true if transform is set to run on a schedule
     */
    public boolean getEnabled()
    {
        return getProperty("result.enabled");
    }

    /**
     *
     * @return true if transform is set for verbose logging
     */
    public boolean getVerboseLogging()
    {
        return getProperty("result.verboseLogging");
    }

    /**
     *
     * @return JSON map of the state saved after the last transform run. Includes row count info,
     * filter values (runId or modifiedSince) for next run, and persisted parameter values for stored procedure transforms.
     */
    public JSONObject getState()
    {
        return getProperty("result.state");
    }

    /**
     *
     * @return The datetime the transform last checked for work
     */
    public Date getLastChecked()
    {
        return getProperty("result.lastChecked");
    }

    /**
     *
     * @return The transform name
     */
    public String getDescriptionId()
    {
        return getProperty("result.descriptionId");
    }
}
