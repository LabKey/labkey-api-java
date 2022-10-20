/*
 * Copyright (c) 2010 LabKey Corporation
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
package org.labkey.remoteapi;

import org.labkey.remoteapi.internal.ResponseUtils;

import java.util.Map;

/**
 * Base class for objects mapped from subsets of response JSON
 */
public abstract class ResponseObject
{
    protected final Map<String, Object> _allProperties;

    public ResponseObject(Map<String, Object> allProperties)
    {
        _allProperties = allProperties == null ? null : ResponseUtils.deepUnmodifiableMap(allProperties);
    }

    protected ResponseObject()
    {
        this(null);
    }

    /**
     * @return the full set of JSON properties returned by the server (unmodifiable)
     */
    public Map<String, Object> getAllProperties()
    {
        return _allProperties;
    }
}
