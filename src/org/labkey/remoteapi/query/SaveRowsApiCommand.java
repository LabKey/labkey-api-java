package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveRowsApiCommand extends PostCommand<SaveRowsApiResponse>
{
    private final List<Command> _commands = new ArrayList<>();
    private Map<String, Object> _extraContext;
    private Boolean _transacted;
    private Boolean _validateOnly;

    public SaveRowsApiCommand(Command... commands)
    {
        super("query", "saveRows.api");
        addCommand(commands);
    }

    public Map<String, Object> getExtraContext()
    {
        return _extraContext;
    }

    public SaveRowsApiCommand setExtraContext(Map<String, Object> extraContext)
    {
        _extraContext = extraContext;
        return this;
    }

    public SaveRowsApiCommand addCommand(Command... commands)
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

    public SaveRowsApiCommand setTransacted(Boolean transacted)
    {
        _transacted = transacted;
        return this;
    }

    public Boolean isValidateOnly()
    {
        return _validateOnly;
    }

    public SaveRowsApiCommand setValidateOnly(Boolean validateOnly)
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
    protected SaveRowsApiResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new SaveRowsApiResponse(text, status, contentType, json);
    }

    public enum CommandType
    {
        Insert,
        Update,
        Delete
    }

    public static class Command
    {
        SaveRowsCommand.AuditBehavior _auditBehavior;
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
            json.put("rows", getRows());

            if (getAuditBehavior() != null)
                json.put("auditBehavior", getAuditBehavior());

            if (getAuditUserComment() != null && !getAuditUserComment().isEmpty())
                json.put("auditUserComment", getAuditUserComment());

            if (getContainerPath() != null && !getContainerPath().isEmpty())
                json.put("containerPath", getContainerPath());

            if (getExtraContext() != null && !getExtraContext().isEmpty())
                json.put("extraContext", getExtraContext());

            if (isSkipReselectRows() != null)
                json.put("skipReselectRows", isSkipReselectRows());

            return json;
        }

        public SaveRowsCommand.AuditBehavior getAuditBehavior()
        {
            return _auditBehavior;
        }

        public void setAuditBehavior(SaveRowsCommand.AuditBehavior auditBehavior)
        {
            _auditBehavior = auditBehavior;
        }

        public String getAuditUserComment()
        {
            return _auditUserComment;
        }

        public void setAuditUserComment(String auditUserComment)
        {
            _auditUserComment = auditUserComment;
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

        public Map<String, Object> getExtraContext()
        {
            return _extraContext;
        }

        public void setExtraContext(Map<String, Object> extraContext)
        {
            _extraContext = extraContext;
        }

        public String getQueryName()
        {
            return _queryName;
        }

        public String getSchemaName()
        {
            return _schemaName;
        }

        public List<Map<String, Object>> getRows()
        {
            return _rows;
        }

        public void setRows(List<Map<String, Object>> rows)
        {
            _rows = rows;
        }

        public Boolean isSkipReselectRows()
        {
            return _skipReselectRows;
        }

        public void setSkipReselectRows(Boolean skipReselectRows)
        {
            _skipReselectRows = skipReselectRows;
        }
    }
}
