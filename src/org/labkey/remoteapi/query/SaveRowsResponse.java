/*
 * Copyright (c) 2008-2025 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.collections.CaseInsensitiveHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Response object for the {@link SaveRowsCommand}, containing results of batch operations executed on the server.
 * This response provides details about the success or failure of each command in the batch, including:
 * <ul>
 *     <li>Whether the transaction was committed</li>
 *     <li>Number of errors encountered</li>
 *     <li>Detailed results for each command executed</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre><code>
 *  SaveRowsCommand cmd = new SaveRowsCommand();
 *  // Add commands to insert/update/delete gene annotations...
 *  SaveRowsResponse response = cmd.execute(connection, "GenomeProject");
 *
 *  if (response.isCommitted())
 *  {
 *      for (SaveRowsResponse.Result result : response.getResults())
 *      {
 *          System.out.println(String.format(
 *              "%s operation affected %d rows in %s.%s",
 *              result.getCommand(),
 *              result.getRowsAffected(),
 *              result.getSchemaName(),
 *              result.getQueryName()
 *          ));
 *
 *          // For detailed examination of affected rows
 *          for (Map&gt;String, Object> row : result.getRows())
 *          {
 *              System.out.println(String.format(
 *                  "Gene %s annotation at position %d-%d",
 *                  row.get("geneName"),
 *                  row.get("start"),
 *                  row.get("end")
 *              ));
 *          }
 *
 *          // Check if operation was audited
 *          if (result.getTransactionAuditId() > 0)
 *          {
 *              System.out.println("Audit record created with ID: " +
 *                result.getTransactionAuditId());
 *          }
 *      }
 *  }
 *  else
 *  {
 *      System.out.println("Transaction failed with " +
 *          response.getErrorCount() + " errors");
 *  }
 * </code></pre>
 */
public class SaveRowsResponse extends CommandResponse
{
    private final boolean _committed;
    private final int _errorCount;
    private final List<Result> _results;

    public SaveRowsResponse(String text, int statusCode, String contentType, JSONObject json)
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
