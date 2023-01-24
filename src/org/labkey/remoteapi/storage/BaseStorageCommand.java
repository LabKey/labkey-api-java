package org.labkey.remoteapi.storage;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

public abstract class BaseStorageCommand extends PostCommand<StorageCommandResponse>
{
    private final StorageRow _storageRow;

    public BaseStorageCommand(String action, StorageRow storageRow)
    {
        super("storage", action);
        _storageRow = storageRow;
    }

    @Override
    public double getRequiredVersion()
    {
        return -1;
    }

    @Override
    protected StorageCommandResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new StorageCommandResponse(text, status, contentType, json);
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
