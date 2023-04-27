package org.labkey.remoteapi.security;

import org.labkey.remoteapi.PostCommand;

import java.util.Objects;

/**
 * Rename a container on the server
 */
public class RenameContainerCommand extends PostCommand<RenameContainerResponse> {
    private String _name;
    private String _title;
    private boolean _addAlias = true;


    public RenameContainerCommand(String name, String title, boolean addAlias)
    {
        super("core", "RenameContainer");
        _name = name;
        _title = title;
        _addAlias = addAlias;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();
        result.put("name", _name);
        result.put("title", _title);
        result.put("addAlias", _addAlias);

        return result;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public boolean is_addAlias() {
        return _addAlias;
    }

    public void set_addAlias(boolean _addAlias) {
        this._addAlias = _addAlias;
    }

    @Override
    protected RenameContainerResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new RenameContainerResponse(text, status, contentType, json);
    }

}
