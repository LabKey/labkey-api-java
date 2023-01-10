package org.labkey.remoteapi.security;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
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
        // stopImpersonatingApi.api, a true API action added 9/22
        super("login", "stopImpersonating.api");
    }

    @Override
    protected HttpUriRequest createRequest(URI uri)
    {
        // Disable redirects just for this request
        HttpPost request = (HttpPost)super.createRequest(uri); // CONSIDER: generify Command with request type?
        RequestConfig oldConfig = request.getConfig();
        RequestConfig.Builder builder = oldConfig != null ? RequestConfig.copy(oldConfig) : RequestConfig.custom();
        builder.setRedirectsEnabled(false);
        request.setConfig(builder.build());

        return request;
    }
}
