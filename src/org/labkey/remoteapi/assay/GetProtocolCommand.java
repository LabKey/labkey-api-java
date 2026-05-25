/*
 * Copyright (c) 2020-2026 LabKey Corporation
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

import org.json.JSONObject;
import org.labkey.remoteapi.GetCommand;

import java.util.Map;

public class GetProtocolCommand extends GetCommand<ProtocolResponse>
{
    private String _providerName;
    private Long _protocolId;
    private Boolean _copy;

    public GetProtocolCommand(String providerName)
    {
        super("assay", "getProtocol");
        _providerName = providerName;
    }

    public GetProtocolCommand(long protocolId)
    {
        super("assay", "getProtocol");
        _protocolId = protocolId;
    }

    public GetProtocolCommand(long protocolId, boolean copy)
    {
        super("assay", "getProtocol");
        _protocolId = protocolId;
        _copy = copy;
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        Map<String, Object> params = super.createParameterMap();
        if (_protocolId != null)
        {
            params.put("protocolId", _protocolId);
            params.put("copy", _copy);
        }
        else if (_providerName != null)
        {
            params.put("providerName", _providerName);
        }
        return params;
    }

    @Override
    protected ProtocolResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ProtocolResponse(text, status, contentType, json);
    }

    public String getProviderName()
    {
        return _providerName;
    }

    public void setProviderName(String providerName)
    {
        _providerName = providerName;
    }

    public Long getProtocolId()
    {
        return _protocolId;
    }

    public void setProtocolId(long protocolId)
    {
        _protocolId = protocolId;
    }

    public Boolean getCopy()
    {
        return _copy;
    }

    public void setCopy(Boolean copy)
    {
        _copy = copy;
    }
}
