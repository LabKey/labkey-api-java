package org.labkey.remoteapi.storage;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Response object used for the storage {@link CreateCommand}, {@link UpdateCommand}, and {@link DeleteCommand}.
 * The response object will include a string message and a data object with the
 * properties from the relevant storage item from the command.
 */
public class StorageCommandResponse extends CommandResponse
{
    private String _message;
    private Map<String, Object> _data;

    /**
     * Constructs a new StorageCommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text          The response text
     * @param statusCode    The HTTP status code
     * @param contentType   The response content type
     * @param json          The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public StorageCommandResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _message = json.get("message").toString();
        _data = new HashMap<>((JSONObject)json.get("data"));
    }

    /**
     * Returns the success message for the command.
     * @return The success message string.
     */
    public String getMessage()
    {
        return _message;
    }

    /**
     * Returns the data object with properties from the newly created storage item.
     * @return A map of the key value pairs for the storage item.
     */
    public Map<String, Object> getData()
    {
        return _data;
    }

    public Integer getRowId()
    {
        if (getData().containsKey("rowId"))
            return Integer.parseInt(getData().get("rowId").toString());

        return null;
    }
}
