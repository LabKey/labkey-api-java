package org.labkey.remoteapi.query;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

/**
 * User: kevink
 * Date: 2/3/17
 */
public class ImportDataResponse extends CommandResponse
{
    private final Boolean _success;
    private final int _rowCount;

    public ImportDataResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
        _success = json.containsKey("success") ? (Boolean)json.get("success") : Boolean.FALSE;
        _rowCount = json.containsKey("rowCount") ? ((Number)json.get("rowCount")).intValue() : 0;
    }

    public Boolean getSuccess()
    {
        return _success;
    }

    public int getRowCount()
    {
        return _rowCount;
    }

}
