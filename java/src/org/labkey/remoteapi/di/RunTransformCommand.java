package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class RunTransformCommand extends BaseTransformCommand<RunTransformResponse>
{

    public RunTransformCommand(RunTransformCommand source)
    {
        super(source);
    }

    /** @param transformId the name of the transform to run */
    public RunTransformCommand(String transformId)
    {
        super("dataintegration", "runTransform");
        _transformId = transformId;
    }

    @Override
    protected RunTransformResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new RunTransformResponse(text, status, contentType, json, this);
    }
}
