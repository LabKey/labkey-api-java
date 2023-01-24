/*
 * Copyright (c) 2013-2019 LabKey Corporation
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

import org.json.JSONObject;

/**
 * Resets the stored state of an ETL, allowing it to be rerun from scratch by forgetting any retained last modified dates
 * or similar.
 */
public class ResetTransformStateCommand extends BaseTransformCommand<ResetTransformStateResponse>
{
    /**
     *  Reset an ETL transform to its initial state, as if it has never been run
     *  @param transformId the name of the transform to reset
     *  */
    public ResetTransformStateCommand(String transformId)
    {
        super("resetTransformState", transformId);
    }

    @Override
    protected ResetTransformStateResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ResetTransformStateResponse(text, status, contentType, json);
    }
}
