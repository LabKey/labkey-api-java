package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command for executing multiple data modification operations (insert, update, delete) in a single request
 * to a LabKey Server. This command allows batching multiple operations together, optionally in a transaction.
 * <p>
 * All data exposed from a LabKey Server is organized into schemas containing queries. Each command in a batch
 * specifies the schema name (e.g., 'lists' or 'study') and query name (e.g., 'People' or 'Samples') to operate on.
 * <p>
 * The command supports several features:
 * <ul>
 *     <li>Multiple operations (insert, update, delete) in a single request</li>
 *     <li>Optional transaction support to ensure all-or-nothing execution</li>
 *     <li>Validation-only mode to check operations without making changes</li>
 *     <li>Audit trail support with configurable detail levels</li>
 *     <li>Custom audit comments for tracking changes</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre><code>
 *  ApiKeyCredentialsProvider credentials = new ApiKeyCredentialsProvider("xxx");
 *  Connection conn = new Connection("http://localhost:8080", credentials);
 *  SaveRowsApiCommand saveCmd = new SaveRowsApiCommand();
 *
 *  // Add new gene annotations
 *  saveCmd.addCommand(new Command(CommandType.Insert, "genome", "GeneAnnotations",
 *      List.of(
 *          Map.of("name", "p53 binding site", "geneName", "TP53", "start", 1000, "end", 1020),
 *          Map.of("name", "TATA box", "geneName", "BRCA1", "start", 2500, "end", 2506)
 *      )));
 *
 *  // Update annotation positions
 *  Command updateCmd = new Command(CommandType.Update, "genome", "GeneAnnotations",
 *      List.of(Map.of(
 *          "name", "Promoter region",
 *          "geneName", "EGFR",
 *          "start", 5000,
 *          "end", 5500
 *      )));
 *  updateCmd.setAuditBehavior(SaveRowsCommand.AuditBehavior.DETAILED);
 *  updateCmd.setAuditUserComment("Updated promoter region coordinates based on new assembly");
 *  saveCmd.addCommand(updateCmd);
 *
 *  // Delete obsolete annotation
 *  saveCmd.addCommand(new Command(CommandType.Delete, "genome", "GeneAnnotations",
 *      List.of(Map.of("name", "Putative enhancer", "geneName", "MYC"))));
 *
 *  // Execute all commands in a transaction
 *  SaveRowsApiResponse response = saveCmd.execute(conn, "GenomeProject");
 * </code></pre>
 */
public class SaveRowsCommand extends PostCommand<SaveRowsResponse>
{
    private final List<Command> _commands = new ArrayList<>();
    private Map<String, Object> _extraContext;
    private Boolean _transacted;
    private Boolean _validateOnly;

    public SaveRowsCommand(Command... commands)
    {
        super("query", "saveRows.api");
        addCommands(commands);
    }

    public Map<String, Object> getExtraContext()
    {
        return _extraContext;
    }

    public SaveRowsCommand setExtraContext(Map<String, Object> extraContext)
    {
        _extraContext = extraContext;
        return this;
    }

    public SaveRowsCommand addCommands(Command... commands)
    {
        for (Command command : commands)
        {
            if (command != null)
                _commands.add(command);
        }

        return this;
    }

    public List<Command> getCommands()
    {
        return _commands;
    }

    public Boolean isTransacted()
    {
        return _transacted;
    }

    public SaveRowsCommand setTransacted(Boolean transacted)
    {
        _transacted = transacted;
        return this;
    }

    public Boolean isValidateOnly()
    {
        return _validateOnly;
    }

    public SaveRowsCommand setValidateOnly(Boolean validateOnly)
    {
        _validateOnly = validateOnly;
        return this;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject json = new JSONObject();

        List<JSONObject> commands = new ArrayList<>();
        for (Command command : getCommands())
            commands.add(command.getJsonObject());
        json.put("commands", commands);

        if (getExtraContext() != null && !getExtraContext().isEmpty())
            json.put("extraContext", getExtraContext());

        if (isTransacted() != null)
            json.put("transacted", isTransacted());

        if (isValidateOnly() != null)
            json.put("validateOnly", isValidateOnly());

        return json;
    }

    @Override
    protected SaveRowsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveRowsResponse(text, status, contentType, json);
    }

    public enum CommandType
    {
        Insert,
        Update,
        Delete
    }

    public static class Command extends BaseRowsCommand
    {
        final CommandType _commandType;
        String _containerPath;
        Boolean _skipReselectRows;

        public Command(CommandType commandType, String schemaName, String queryName, List<Map<String, Object>> rows)
        {
            super(schemaName, queryName, null);
            assert null != commandType;
            _commandType = commandType;
            setRows(rows);
        }

        public JSONObject getJsonObject()
        {
            JSONObject json = super.getJsonObject();
            json.put("command", getCommandType().name().toLowerCase());

            if (getContainerPath() != null && !getContainerPath().isEmpty())
                json.put("containerPath", getContainerPath());

            if (isSkipReselectRows() != null)
                json.put("skipReselectRows", isSkipReselectRows());

            return json;
        }

        public CommandType getCommandType()
        {
            return _commandType;
        }

        public String getContainerPath()
        {
            return _containerPath;
        }

        public void setContainerPath(String containerPath)
        {
            _containerPath = containerPath;
        }

        public Boolean isSkipReselectRows()
        {
            return _skipReselectRows;
        }

        public void setSkipReselectRows(Boolean skipReselectRows)
        {
            _skipReselectRows = skipReselectRows;
        }

        @Override
        public String getActionName()
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public String getControllerName()
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public RowsResponse execute(Connection connection, String folderPath) throws IOException, CommandException
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public double getRequiredVersion()
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public void setRequiredVersion(double requiredVersion)
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public Integer getTimeout()
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        @Override
        public void setTimeout(Integer timeout)
        {
            throw new UnsupportedOperationException(unsupportedMethodMessage());
        }

        private String unsupportedMethodMessage()
        {
            // "Command does not support methodName()."
            return Command.class.getSimpleName() + " does not support " + new Throwable().getStackTrace()[1].getMethodName() + "().";
        }
    }
}
