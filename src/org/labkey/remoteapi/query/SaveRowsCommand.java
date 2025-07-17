package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

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
 *  updateCmd.setAuditBehavior(BaseRowsCommand.AuditBehavior.DETAILED);
 *  updateCmd.setAuditUserComment("Updated promoter region coordinates based on new assembly");
 *  saveCmd.addCommands(updateCmd);
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

    /**
     * Returns the extra context map containing additional parameters for the save operation.
     * This context can be used to pass additional information to the server during the save process.
     * @return Map containing extra context parameters, or null if no extra context is set
     */
    public Map<String, Object> getExtraContext()
    {
        return _extraContext;
    }

    /**
     * Sets additional context parameters for the save operation.
     * @param extraContext Map containing extra parameters to be passed to the server
     * @return This SaveRowsCommand instance for method chaining
     */
    public SaveRowsCommand setExtraContext(Map<String, Object> extraContext)
    {
        _extraContext = extraContext;
        return this;
    }

    /**
     * Adds one or more Command objects to the set of commands to be executed by this SaveRowsCommand.
     * @param commands The commands to add to this SaveRowsCommand.
     * @return This SaveRowsCommand instance for method chaining
     */
    public SaveRowsCommand addCommands(Command... commands)
    {
        for (Command command : commands)
        {
            if (command != null)
                _commands.add(command);
        }
        return this;
    }

    /**
     * Returns the list of Command objects representing the batch operations to be executed.
     * Each Command in the list represents a single insert, update, or delete operation.
     * @return List of Command objects to be executed
     */
    public List<Command> getCommands()
    {
        return _commands;
    }

    /**
     * Checks if the operations should be executed in a transaction.
     * When true, all operations will be executed atomically - either all succeed or all fail.
     * @return Boolean indicating if operations should be transacted, or null for default behavior
     */
    public Boolean isTransacted()
    {
        return _transacted;
    }

    /**
     * Sets whether the operations should be executed in a transaction.
     * @param transacted When true, all operations will be executed atomically.
     *                   When false, operations may partially succeed.
     *                   When null, uses server default behavior.
     * @return This SaveRowsCommand instance for method chaining
     */
    public SaveRowsCommand setTransacted(Boolean transacted)
    {
        _transacted = transacted;
        return this;
    }

    /**
     * Checks if this is a validation-only operation.
     * When true, the server will validate the operations without making any actual changes.
     * @return Boolean When true, validates operations without making changes.
     *                 When false, executes operations normally.
     *                 When null, uses server default behavior.
     */
    public Boolean isValidateOnly()
    {
        return _validateOnly;
    }

    /**
     * Sets whether this should be a validation-only operation.
     * @param validateOnly When true, validates operations without making changes.
     *                     When false, executes operations normally.
     *                     When null, uses server default behavior.
     * @return This SaveRowsCommand instance for method chaining
     */
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

    // N.B. You may be inclined to have this share implementation with BaseRowsCommand; however, I would caution
    // against doing so. This class does not represent a command like a PostCommand or a GetCommand but rather
    // aligns with the "commands" made on a request to the save rows endpoint.
    /**
     * Represents a single command operation of a specified type
     * (e.g., insert, update, delete) to be executed within a {@link SaveRowsCommand}.
     */
    public static class Command
    {
        BaseRowsCommand.AuditBehavior _auditBehavior;
        String _auditUserComment;
        final CommandType _commandType;
        String _containerPath;
        Map<String, Object> _extraContext;
        List<Map<String, Object>> _rows;
        final String _queryName;
        final String _schemaName;
        Boolean _skipReselectRows;

        public Command(CommandType commandType, String schemaName, String queryName, List<Map<String, Object>> rows)
        {
            assert null != commandType;
            assert null != schemaName && !schemaName.isEmpty();
            assert null != queryName && !queryName.isEmpty();

            _commandType = commandType;
            _schemaName = schemaName;
            _queryName = queryName;
            _rows = rows;
        }

        public JSONObject getJsonObject()
        {
            JSONObject json = new JSONObject();

            json.put("command", getCommandType().name().toLowerCase());
            json.put("schemaName", getSchemaName());
            json.put("queryName", getQueryName());
            json.put("rows", BaseRowsCommand.rowsToJson(getRows()));

            if (getAuditBehavior() != null)
                json.put("auditBehavior", getAuditBehavior());

            BaseRowsCommand.stringToJson(json, "auditUserComment", getAuditUserComment());
            BaseRowsCommand.stringToJson(json, "containerPath", getContainerPath());

            if (getExtraContext() != null && !getExtraContext().isEmpty())
                json.put("extraContext", getExtraContext());

            if (isSkipReselectRows() != null)
                json.put("skipReselectRows", isSkipReselectRows());

            return json;
        }

        /**
         * Gets the audit behavior setting for this command.
         * Determines the level of detail in the audit log for this operation.
         * @return The current audit behavior setting, or null if using default behavior
         */
        public BaseRowsCommand.AuditBehavior getAuditBehavior()
        {
            return _auditBehavior;
        }

        /**
         * Sets the audit behavior for this command.
         * @param auditBehavior The desired audit behavior
         * @return This Command instance for method chaining
         */
        public Command setAuditBehavior(BaseRowsCommand.AuditBehavior auditBehavior)
        {
            _auditBehavior = auditBehavior;
            return this;
        }

        /**
         * Gets the user-provided comment that will be included in the audit log.
         * @return The audit comment, or null if none was set
         */
        public String getAuditUserComment()
        {
            return _auditUserComment;
        }

        /**
         * Sets a user comment to be included in the audit log for this command.
         * @param auditUserComment The comment to include in the audit log
         * @return This Command instance for method chaining
         */
        public Command setAuditUserComment(String auditUserComment)
        {
            _auditUserComment = auditUserComment;
            return this;
        }

        /**
         * Gets the type of operation this command represents.
         * @return The CommandType for this command
         */
        public CommandType getCommandType()
        {
            return _commandType;
        }

        /**
         * Gets the container path where this command should be executed.
         * @return The container path, or null if using the default container
         */
        public String getContainerPath()
        {
            return _containerPath;
        }

        /**
         * Sets the container path where this command should be executed.
         * @param containerPath The target container path
         * @return This Command instance for method chaining
         */
        public Command setContainerPath(String containerPath)
        {
            _containerPath = containerPath;
            return this;
        }

        /**
         * Gets additional context parameters specific to this command.
         * @return Map of extra context parameters, or null if none are set
         */
        public Map<String, Object> getExtraContext()
        {
            return _extraContext;
        }

        /**
         * Sets additional context parameters for this specific command.
         * @param extraContext Map of extra parameters to be passed with this command
         * @return This Command instance for method chaining
         */
        public Command setExtraContext(Map<String, Object> extraContext)
        {
            _extraContext = extraContext;
            return this;
        }

        /**
         * Gets the name of the query this command operates on.
         * @return The query name
         */
        public String getQueryName()
        {
            return _queryName;
        }

        /**
         * Gets the name of the schema containing the query.
         * @return The schema name
         */
        public String getSchemaName()
        {
            return _schemaName;
        }

        /**
         * Gets the list of rows to be processed by this command.
         * Each row is represented as a Map of column names to values.
         * @return List of rows to be processed
         */
        public List<Map<String, Object>> getRows()
        {
            return _rows;
        }

        /**
         * Sets the list of rows to be processed by this command.
         * @param rows List of maps where each map represents a row with column names as keys
         * @return This Command instance for method chaining
         */
        public Command setRows(List<Map<String, Object>> rows)
        {
            _rows = rows;
            return this;
        }

        /**
         * Checks if the command should skip re-selecting rows after the operation.
         * @return Boolean indicating whether to skip row re-selection or null for default behavior
         */
        public Boolean isSkipReselectRows()
        {
            return _skipReselectRows;
        }

        /**
         * Sets whether to skip re-selecting rows after the operation completes.
         * @param skipReselectRows When true, skips row reselection after the operation
         *                         When false, performs row reselection
         *                         When null, uses server default behavior
         * @return This Command instance for method chaining
         */
        public Command setSkipReselectRows(Boolean skipReselectRows)
        {
            _skipReselectRows = skipReselectRows;
            return this;
        }
    }
}
