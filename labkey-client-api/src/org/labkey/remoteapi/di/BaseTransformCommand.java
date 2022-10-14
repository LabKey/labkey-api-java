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

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

public abstract class BaseTransformCommand<ResponseType extends CommandResponse> extends PostCommand<ResponseType>
{
    private String _transformId;

    public BaseTransformCommand(BaseTransformCommand<ResponseType> source)
    {
        super(source);
        setTransformId(source.getTransformId());
    }

    public BaseTransformCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    public String getTransformId()
    {
        return _transformId;
    }

    public void setTransformId(String transformId)
    {
        _transformId = transformId;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();
        if (result == null)
        {
            result = new JSONObject();
        }
        result.put("transformId", _transformId);
        setJsonObject(result);
        return result;
    }

    @Override
    public BaseTransformCommand<ResponseType> copy()
    {
        return new TransformCommandCopy<>(this);
    }
}

class TransformCommandCopy<R extends CommandResponse> extends BaseTransformCommand<R>
{
    public TransformCommandCopy(BaseTransformCommand<R> source)
    {
        super(source);
    }
}
