/*
 * Copyright (c) 2009 LabKey Corporation
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

package org.labkey.remoteapi.security;

import org.labkey.remoteapi.PostCommand;

import org.json.JSONObject;

/**
 * Rename a container on the server
 */
public class RenameContainerCommand extends PostCommand<RenameContainerResponse> {
    private String _name;
    private String _title;
    private boolean _addAlias = true;


    public RenameContainerCommand(String name, String title, boolean addAlias)
    {
        super("admin", "RenameContainer");
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
