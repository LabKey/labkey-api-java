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
package org.labkey.remoteapi.assay;

import org.labkey.remoteapi.CommandResponse;

import java.util.Map;
import java.util.List;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 1:59:49 PM
*/
public class AssayListResponse extends CommandResponse
{
    public AssayListResponse(String text, int statusCode)
    {
        super(text, statusCode);
    }

    List<Map<String,Object>> getDefinitions()
    {
        return getProperty("definitions");
    }

    Map<String,Object> getDefinition(String name)
    {
        return findObject(getDefinitions(), "name", name);
    }

    Map<String,Object> getDefinition(int id)
    {
        return findObject(getDefinitions(), "id", String.valueOf(id));
    }
}