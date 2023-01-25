package org.labkey.remoteapi;

import org.apache.hc.client5.http.classic.methods.HttpGet;

import java.net.URI;

/** Base class for all commands that use get **/
public abstract class GetCommand<ResponseType extends CommandResponse> extends Command<ResponseType, HttpGet>
{
    protected GetCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    /**
     * Creates the {@link HttpGet} instance used for the request. Override to modify the HttpGet object before use.
     * @param uri the request uri
     * @return The HttpUriRequest instance.
     */
    @Override
    protected HttpGet createRequest(URI uri)
    {
        return new HttpGet(uri);
    }
}
