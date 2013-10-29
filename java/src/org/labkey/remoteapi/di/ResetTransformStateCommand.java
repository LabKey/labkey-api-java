package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class ResetTransformStateCommand extends BaseTransformCommand<ResetTransformStateResponse>
{
    public ResetTransformStateCommand(RunTransformCommand source)
    {
        super(source);
    }

    /** @param transformId the name of the transform to run */
    public ResetTransformStateCommand(String transformId)
    {
        super("dataintegration", "resetTransformState");
        _transformId = transformId;
    }

    @Override
    protected ResetTransformStateResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ResetTransformStateResponse(text, status, contentType, json, this);
    }

}
