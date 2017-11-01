/*
 * Copyright (c) 2009-2017 LabKey Corporation
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

import java.util.Map;

/**
 * User: adam
 * Date: Feb 23, 2009
 * Time: 1:13:26 PM
 */
public class ApiVersionException extends CommandException
{
    /**
     * @deprecated Use {@link #ApiVersionException(String, int, Map, String, String)}
     */
    ApiVersionException(String message, int statusCode, Map<String, Object> properties, String responseText)
    {
        this(message, statusCode, properties, responseText, null);
    }

    ApiVersionException(String message, int statusCode, Map<String, Object> properties, String responseText, String contentType)
    {
        super(message, statusCode, properties, responseText, contentType);
    }
}
