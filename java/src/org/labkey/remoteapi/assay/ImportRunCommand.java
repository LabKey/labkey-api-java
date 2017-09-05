/*
 * Copyright (c) 2012-2017 LabKey Corporation
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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.io.File;
import java.net.URI;
import java.util.List;
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

    // Only one of the follow is allowed
    private List<Map<String, Object>> _dataRows;
    private File _file;
    private String _runFilePath;

    public ImportRunCommand(int assayId)
    {
        this(assayId, (File)null);
    }

    public ImportRunCommand(int assayId, File file)
    {
        super("assay", "importRun.api");
        _assayId = assayId;
        _file = file;
    }

    public ImportRunCommand(int assayId, List<Map<String, Object>> dataRows)
    {
        super("assay", "importRun.api");
        _assayId = assayId;
        _dataRows = dataRows;
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

    /**
     * Absolute or relative path to assay data file to be imported.
     * The file must exist under the file or pipeline root of the container.
     * @param runFilePath the data file path
     */
    public void setRunFilePath(String runFilePath)
    {
        _runFilePath = runFilePath;
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
        cmd._runFilePath = this._runFilePath;

        return cmd;
    }

    @Override
    protected ImportRunResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ImportRunResponse(text, status, contentType, json, this);
    }

    @Override
    protected HttpUriRequest createRequest(URI uri)
    {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        if (_assayId == 0)
            throw new IllegalArgumentException("assay id required");

        if (_file == null && _runFilePath == null && _dataRows == null)
            throw new IllegalArgumentException("At least one of 'file', 'runFilePath', or 'dataRows' is required");

        if ((_file != null ? 1 : 0) + (_runFilePath != null ? 1 : 0) + (_dataRows != null ? 1 : 0) > 1)
            throw new IllegalArgumentException("Only one of 'file', 'runFilePath', or 'dataRows' is allowed");

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
            if (_dataRows != null)
                json.put("dataRows", _dataRows);
            if (_runFilePath != null)
                json.put("runFilePath", _runFilePath);

            builder.addTextBody("json", json.toJSONString(), ContentType.APPLICATION_JSON);
        }
        else
        {
            builder.addTextBody("assayId", String.valueOf(_assayId));
            if (_batchId > 0)
                builder.addTextBody("batchId", String.valueOf(_batchId));
            if (_name != null)
                builder.addTextBody("name", String.valueOf(_name));
            if (_comment != null)
                builder.addTextBody("comment", String.valueOf(_comment));
            if (_properties != null)
            {
                for (Map.Entry<String, Object> entry : _properties.entrySet())
                {
                    builder.addTextBody("properties[" + entry.getKey() + "]", String.valueOf(entry.getValue()));
                }
            }
            if (_batchProperties != null)
            {
                for (Map.Entry<String, Object> entry : _batchProperties.entrySet())
                {
                    builder.addTextBody("batchProperties[" + entry.getKey() + "]", String.valueOf(entry.getValue()));
                }
            }
            if (_dataRows != null)
                builder.addTextBody("dataRows", JSONArray.toJSONString(_dataRows), ContentType.APPLICATION_JSON);
            if (_runFilePath != null)
                builder.addTextBody("runFilePath", _runFilePath);
        }

        // UNDONE: set content type based on extension
        if (_file != null)
            builder.addBinaryBody("file", _file, ContentType.APPLICATION_OCTET_STREAM, _file.getName());

        HttpPost post = new HttpPost(uri);
        post.setEntity(builder.build());
        return post;
    }
}
