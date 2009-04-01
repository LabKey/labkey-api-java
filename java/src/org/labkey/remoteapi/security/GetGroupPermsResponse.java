/*
 * Copyright (c) 2008-2009 LabKey Corporation
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

import org.labkey.remoteapi.CommandResponse;
import org.json.simple.JSONObject;

/*
* User: Dave
* Date: Sep 9, 2008
* Time: 2:15:51 PM
*/
public class GetGroupPermsResponse extends CommandResponse
{
    public GetGroupPermsResponse(String text, int statusCode, String contentType, JSONObject json, GetGroupPermsCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }
    
}