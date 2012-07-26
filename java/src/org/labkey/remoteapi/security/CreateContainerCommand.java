/*
 * Copyright (c) 2012 LabKey Corporation
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

import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;


/**
 * Create a container (project/folder/workbook) on the server
 * User: jeckels
 * Date: Jul 20, 2012
 */
public class CreateContainerCommand extends PostCommand<CreateContainerResponse>
{
    private String _name;
    private String _title;
    private String _description;
    private boolean _workbook = false;
    private String _folderType;

    public CreateContainerCommand(CreateContainerCommand source)
    {
        super(source);
        _name = source.getName();
        _title = source.getTitle();
        _description = source.getDescription();
        _workbook = source.isWorkbook();
        _folderType = source.getFolderType();
    }

    /** @param name the name of the container to create */  
    public CreateContainerCommand(String name)
    {
        super("core", "createContainer");
        _name = name;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = super.getJsonObject();
        if (result == null)
        {
            result = new JSONObject();
        }
        result.put("name", _name);
        result.put("title", _title);
        result.put("description", _description);
        result.put("isWorkbook", _workbook);
        result.put("folderType", _folderType);
        setJsonObject(result);
        return result;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String title)
    {
        _title = title;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String description)
    {
        _description = description;
    }

    public boolean isWorkbook()
    {
        return _workbook;
    }

    public void setWorkbook(boolean workbook)
    {
        _workbook = workbook;
    }

    public String getFolderType()
    {
        return _folderType;
    }

    public void setFolderType(String folderType)
    {
        _folderType = folderType;
    }

    @Override
    protected CreateContainerResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new CreateContainerResponse(text, status, contentType, json, this);
    }

    @Override
    public CreateContainerCommand copy()
    {
        return new CreateContainerCommand(this);
    }
}
