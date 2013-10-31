/*
 * Copyright (c) 2013 LabKey Corporation
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

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class RunTransformCommand extends BaseTransformCommand<RunTransformResponse>
{

    public RunTransformCommand(RunTransformCommand source)
    {
        super(source);
    }

    /** @param transformId the name of the transform to run */
    public RunTransformCommand(String transformId)
    {
        super("dataintegration", "runTransform");
        _transformId = transformId;
    }

    @Override
    protected RunTransformResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new RunTransformResponse(text, status, contentType, json, this);
    }
}
