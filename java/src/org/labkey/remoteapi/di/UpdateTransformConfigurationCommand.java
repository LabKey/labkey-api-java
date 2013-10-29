package org.labkey.remoteapi.di;

import org.json.simple.JSONObject;

/**
 * User: tgaluhn
 * Date: 10/29/13
 */
public class UpdateTransformConfigurationCommand extends BaseTransformCommand<UpdateTransformConfigurationResponse>
{
    private Boolean enabled = null;
    private Boolean verboseLogging = null;

    public UpdateTransformConfigurationCommand(UpdateTransformConfigurationCommand source)
    {
        super(source);
    }

    /** @param transformId the name of the transform to run */
    public UpdateTransformConfigurationCommand(String transformId)
    {
        super("dataintegration", "UpdateTransformConfiguration");
        _transformId = transformId;
    }

    @Override
    protected UpdateTransformConfigurationResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new UpdateTransformConfigurationResponse(text, status, contentType, json, this);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();
        if (enabled != null)
            result.put("enabled", enabled);
        if (verboseLogging != null)
            result.put("verboseLogging", verboseLogging);

        return result;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Boolean getVerboseLogging()
    {
        return verboseLogging;
    }

    public void setVerboseLogging(boolean verboseLogging)
    {
        this.verboseLogging = verboseLogging;
    }


}
