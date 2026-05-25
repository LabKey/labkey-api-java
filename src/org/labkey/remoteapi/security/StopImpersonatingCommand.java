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
package org.labkey.remoteapi.security;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

import java.net.URI;

/**
 * Stop impersonating a user, group, or role(s).
 */
public class StopImpersonatingCommand extends PostCommand<CommandResponse>
{
    public StopImpersonatingCommand()
    {
        // TODO: At some point (when we're okay with breaking compatibility with pre-22.10 servers) switch this to
        // stopImpersonatingApi.api, a true API action added 9/22 and then remove the redirect machinations below.
        super("login", "stopImpersonating.api");
    }

    @Override
    protected HttpPost createRequest(URI uri)
    {
        // Disable redirects just for this request
        HttpPost request = super.createRequest(uri);
        RequestConfig oldConfig = request.getConfig();
        RequestConfig.Builder builder = oldConfig != null ? RequestConfig.copy(oldConfig) : RequestConfig.custom();
        builder.setRedirectsEnabled(false);
        request.setConfig(builder.build());

        return request;
    }
}
