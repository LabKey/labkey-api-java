/*
 * Copyright (c) 2008 LabKey Corporation
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

import org.apache.commons.httpclient.HttpException;

import java.util.Map;

/*
* User: Dave
* Date: Jul 11, 2008
* Time: 1:35:07 PM
*/
public class CommandException extends HttpException
{
    private int _statusCode;
    private Map<String,Object> _properties;

    public CommandException(String message, int statusCode, Map<String,Object> properties)
    {
        super(message);
        _statusCode = statusCode;
        _properties = properties;
    }

    public int getStatusCode()
    {
        return _statusCode;
    }

    public Map<String, Object> getProperties()
    {
        return _properties;
    }
}