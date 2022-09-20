package org.labkey.remoteapi.storage;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

public abstract class BaseStorageCommand extends PostCommand<StorageCommandResponse>
{
    private StorageRow _storageRow;

    public BaseStorageCommand(String action, StorageRow storageRow)
    {
        super("storage", action);
        this._storageRow = storageRow;
    }

    @Override
    public double getRequiredVersion()
    {
        return -1;
    }

    @Override
    protected StorageCommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new StorageCommandResponse(text, status, contentType, json, this);
    }

    /**
     * Dynamically builds the JSON object to send based on the storageRow.
     * @return The JSON object to send.
     */
    @Override
    public JSONObject getJsonObject()
    {
        return _storageRow.toJsonObject();
    }
}
