package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.collections.CaseInsensitiveHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuerySaveRowsResponse extends CommandResponse
{
    private final boolean _committed;
    private final int _errorCount;
    private final List<Result> _results;

    public QuerySaveRowsResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        super(text, statusCode, contentType, json);

        _committed = json.optBoolean("committed", false);
        _errorCount = json.optInt("errorCount", 0);

        List<Result> results = new ArrayList<>();
        if (json.has("result"))
        {
            for (Object resultJson : json.getJSONArray("result"))
                results.add(new Result((JSONObject) resultJson));
        }
        _results = Collections.unmodifiableList(results);
    }

    public boolean isCommitted()
    {
        return _committed;
    }

    public int getErrorCount()
    {
        return _errorCount;
    }

    public List<Result> getResults()
    {
        return _results;
    }

    public static class Result
    {
        private final String _command;
        private final String _containerPath;
        private final String _queryName;
        private final List<Map<String, Object>> _rows = new ArrayList<>();
        private final int _rowsAffected;
        private final String _schemaName;
        private final int _transactionAuditId;

        private Result(JSONObject json)
        {
            _command = json.optString("command", null);
            _containerPath = json.optString("containerPath", null);
            _queryName = json.optString("queryName", null);
            _rowsAffected = json.optInt("rowsAffected", 0);
            _schemaName = json.optString("schemaName", null);
            _transactionAuditId = json.optInt("transactionAuditId", 0);

            if (json.has("rows"))
            {
                for (Object rowJson : json.getJSONArray("rows"))
                    _rows.add(new CaseInsensitiveHashMap<>(((JSONObject) rowJson).toMap()));
            }
        }

        public String getCommand()
        {
            return _command;
        }

        public String getContainerPath()
        {
            return _containerPath;
        }

        public String getQueryName()
        {
            return _queryName;
        }

        public List<Map<String, Object>> getRows()
        {
            return _rows;
        }

        public int getRowsAffected()
        {
            return _rowsAffected;
        }

        public String getSchemaName()
        {
            return _schemaName;
        }

        public int getTransactionAuditId()
        {
            return _transactionAuditId;
        }
    }
}
