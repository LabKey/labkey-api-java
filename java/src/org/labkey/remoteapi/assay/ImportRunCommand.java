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
package org.labkey.remoteapi.assay;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.PostCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Import a new assay run.  If no batch is specified, a new batch will be inserted.
 *
 * User: kevink
 * Date: 9/12/12
 */
public class ImportRunCommand extends PostCommand<ImportRunResponse>
{
    private boolean _useJson = false;
    private int _batchId;
    private int _assayId;
    private String _name;
    private String _comment;
    private Map<String, Object> _properties;
    private Map<String, Object> _batchProperties;
    private File _file;

    public ImportRunCommand(int assayId, File file)
    {
        super("assay", "importRun");
        _assayId = assayId;
        _file = file;
    }

    public void setUseJson(boolean useJson)
    {
        _useJson = useJson;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public void setComment(String comment)
    {
        _comment = comment;
    }

    public void setFile(File file)
    {
        _file = file;
    }

    public void setProperties(Map<String, Object> properties)
    {
        _properties = properties;
    }

    public void setBatchId(int batchId)
    {
        _batchId = batchId;
    }

    public void setBatchProperties(Map<String, Object> batchProperties)
    {
        _batchProperties = batchProperties;
    }

    @Override
    public ImportRunCommand copy()
    {
        ImportRunCommand cmd = new ImportRunCommand(_assayId, _file);
        cmd._useJson = this._useJson;
        cmd._name = this._name;
        cmd._comment = this._comment;
        cmd._properties = this._properties;
        cmd._batchId = this._batchId;
        cmd._batchProperties = this._batchProperties;

        return cmd;
    }

    @Override
    protected ImportRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ImportRunResponse(text, status, contentType, json, this);
    }

    @Override
    protected HttpMethod createMethod()
    {
        MultipartPostMethod method = new MultipartPostMethod();

        if (_assayId == 0)
            throw new IllegalArgumentException("assay id required");

        if (_file == null)
            throw new IllegalArgumentException("file required");

        if (_useJson)
        {
            JSONObject json = new JSONObject();
            json.put("apiVersion", getRequiredVersion());
            json.put("assayId", _assayId);
            if (_batchId > 0)
                json.put("batchId", _batchId);
            if (_name != null)
                json.put("name", _name);
            if (_comment != null)
                json.put("comment", _comment);
            if (_properties != null)
                json.put("properties", _properties);
            if (_batchProperties != null)
                json.put("batchProperties", _batchProperties);

            StringPart jsonPart = new StringPart("json", json.toJSONString(), "UTF-8");
            jsonPart.setContentType(Command.CONTENT_TYPE_JSON);
            method.addPart(jsonPart);
        }
        else
        {
            method.addPart(new StringPart("assayId", String.valueOf(_assayId)));
            if (_batchId > 0)
                method.addPart(new StringPart("batchId", String.valueOf(_batchId)));
            if (_name != null)
                method.addPart(new StringPart("name", String.valueOf(_name)));
            if (_comment != null)
                method.addPart(new StringPart("comment", String.valueOf(_comment)));
            if (_properties != null)
            {
                for (Map.Entry<String, Object> entry : _properties.entrySet())
                {
                    method.addPart(new StringPart("properties[" + entry.getKey() + "]", String.valueOf(entry.getValue())));
                }
            }
            if (_batchProperties != null)
            {
                for (Map.Entry<String, Object> entry : _batchProperties.entrySet())
                {
                    method.addPart(new StringPart("batchProperties[" + entry.getKey() + "]", String.valueOf(entry.getValue())));
                }
            }
        }

        try
        {
            // UNDONE: set content type based on extension
            FilePart filePart = new FilePart("file", _file, "application/octet-stream", "UTF-8");
            method.addPart(filePart);
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalArgumentException(e);
        }

        return method;
    }

}
