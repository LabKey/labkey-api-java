/*
 * Copyright (c) 2022-2026 LabKey Corporation
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
package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.internal.ResponseUtils;

import java.util.Map;

public class DomainDetailsResponse extends CommandResponse
{
    private final Domain _domain;
    private final String _kind;
    private final Map<String, Object> _options;

    public DomainDetailsResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);
        _domain = new Domain(json.getJSONObject("domainDesign"));
        _kind = json.optString("domainKindName", null);
        _options = ResponseUtils.deepUnmodifiableMap(json.optJSONObject("options"));
    }

    /**
     * Get the Domain deserialized from the server's response
     * @return Domain definition
     */
    public Domain getDomain()
    {
        return _domain;
    }

    /**
     * @return domain kind name returned from the server
     */
    public String getKind()
    {
        return _kind;
    }

    /**
     * @return Domain options returned from the server
     */
    public Map<String, Object> getOptions()
    {
        return _options;
    }
}
