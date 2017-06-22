/*
 * Copyright (c) 2017 LabKey Corporation
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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.io.File;
import java.net.URI;
import java.util.Objects;

/**
 * Import data in bulk from a text, local file, or a file
 * contained within a module on the LabKey Server.
 *
 * User: kevink
 * Date: 2/3/17
 */
public class ImportDataCommand extends PostCommand<ImportDataResponse>
{
    private final String _schemaName;
    private final String _queryName;

    // Data type is one of 'text', 'path', 'moduleResource', or 'file' and is required.
    private String _dataType;
    private Object _dataValue;
    private String _module;

    public boolean _importIdentity;
    public boolean _importLookupByAlternateKey;


    /**
     * Constructs an ImportDataCommand for the given schemaName and queryName.
     * @param schemaName The schemaName
     * @param queryName The queryName.
     */
    public ImportDataCommand(String schemaName, String queryName)
    {
        super("query", "import.api");
        _schemaName = schemaName;
        _queryName = queryName;
    }

    public ImportDataCommand(ImportDataCommand source)
    {
        super(source);
        _schemaName = source._schemaName;
        _queryName = source._queryName;
    }

    /**
     * Returns the schema name.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    /**
     * Returns the query name
     * @return the query name.
     */
    public String getQueryName()
    {
        return _queryName;
    }

    public void setText(String text)
    {
        Objects.requireNonNull(text);
        _dataType = "text";
        _dataValue = text;
    }

    public void setPath(String path)
    {
        Objects.requireNonNull(path);
        _dataType = "path";
        _dataValue = path;
    }

    public void setModuleResource(String module, String moduleResource)
    {
        Objects.requireNonNull(moduleResource);
        _dataType = "moduleResource";
        _dataValue = moduleResource;
        _module = module;
    }

    public void setFile(File file)
    {
        Objects.requireNonNull(file);
        _dataType = "file";
        _dataValue = file;
    }

    public boolean isImportIdentity()
    {
        return _importIdentity;
    }

    public void setImportIdentity(boolean importIdentity)
    {
        _importIdentity = importIdentity;
    }

    public boolean isImportLookupByAlternateKey()
    {
        return _importLookupByAlternateKey;
    }

    public void setImportLookupByAlternateKey(boolean importLookupByAlternateKey)
    {
        _importLookupByAlternateKey = importLookupByAlternateKey;
    }

    @Override
    public JSONObject getJsonObject()
    {
        throw new IllegalStateException();
    }

    @Override
    protected HttpUriRequest createRequest(URI uri)
    {
        Objects.requireNonNull(_schemaName, "schemaName required");
        Objects.requireNonNull(_queryName, "queryName required");

        if (!"text".equals(_dataType) && !"path".equals(_dataType) && !"moduleResource".equals(_dataType) && !"file".equals(_dataType))
            throw new IllegalArgumentException("One of 'text', 'path', 'moduleResource', or 'file' is required");
        Objects.requireNonNull(_dataValue, "Value for '" + _dataType + "' must not be null");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody("schemaName", _schemaName);
        builder.addTextBody("queryName", _queryName);
        builder.addTextBody("importIdentity", Boolean.toString(_importIdentity));
        builder.addTextBody("importLookupByAlternateKey", Boolean.toString(_importLookupByAlternateKey));
        if (_module != null)
            builder.addTextBody("module", _module);
        if (_dataType.equals("file"))
            builder.addBinaryBody(_dataType, (File)_dataValue, ContentType.APPLICATION_OCTET_STREAM, ((File)_dataValue).getName());
        else
            builder.addTextBody(_dataType, (String)_dataValue);

        HttpPost post = new HttpPost(uri);
        // Setting this header forces the ExtFormResponseWriter to return application/json contentType instead of text/html.
        post.addHeader("X-Requested-With", "XMLHttpRequest");
        post.setEntity(builder.build());
        return post;
    }

    @Override
    protected ImportDataResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ImportDataResponse(text, status, contentType, json, this);
    }

    @Override
    public ImportDataCommand copy()
    {
        return new ImportDataCommand(this);
    }
}
