package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.PostCommand;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public abstract class BaseTransformCommand<ResponseType extends CommandResponse> extends PostCommand<ResponseType>
{
    protected String _transformId;

    public BaseTransformCommand(BaseTransformCommand source)
    {
        super(source);
        _transformId = (source.getTransformId());
    }

    public BaseTransformCommand(String controllerName, String actionName)
    {
        super(controllerName, actionName);
    }

    public String getTransformId()
    {
        return _transformId;
    }

    public void setTransformId(String transformId)
    {
        _transformId = transformId;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();
        if (result == null)
        {
            result = new JSONObject();
        }
        result.put("transformId", _transformId);
        setJsonObject(result);
        return result;
    }
}
