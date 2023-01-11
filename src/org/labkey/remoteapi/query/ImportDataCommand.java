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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.io.File;
import java.net.URI;
import java.util.Objects;

/**
 * Import data in bulk from a text, local file, or a file
 * contained within a module on the LabKey Server.
 */
public class ImportDataCommand extends PostCommand<ImportDataResponse>
{
    public enum ImportDataType
    {
        text, path, moduleResource, file
    }

    public enum InsertOption
    {
        /**
         * Bulk insert.
         */
        IMPORT,

        /**
         * Bulk insert or update.
         * <br>
         * NOTE: Not supported for all tables -- tables with auto-increment primary keys in particular.
         * See <a href="https://www.labkey.org/home/Developer/issues/issues-details.view?issueId=42788">Issue 42788</a> for details.
         */
        MERGE,
    }

    private final String _schemaName;
    private final String _queryName;

    private ImportDataType _dataType;
    // Data file format. e.g. "tsv" or "csv"
    private String _format;
    private Object _dataValue;
    private String _module;
    private InsertOption _insertOption;

    private Boolean _useAsync;
    private Boolean _saveToPipeline;

    public Boolean _importIdentity;
    public Boolean _importLookupByAlternateKey;


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

    /**
     * Submits tsv or csv data as text to the server for import.
     * @see #setFormat(String)
     */
    public void setText(String text)
    {
        Objects.requireNonNull(text);
        _dataType = ImportDataType.text;
        _dataValue = text;
    }

    /**
     * Import data from a file relative from the server's webdav root.
     */
    public void setPath(String path)
    {
        Objects.requireNonNull(path);
        _dataType = ImportDataType.path;
        _dataValue = path;
    }

    /**
     * Import data from a resource file embedded in a module.
     * @param module module name
     * @param moduleResource path to the file from the exploded module's directory
     */
    public void setModuleResource(String module, String moduleResource)
    {
        Objects.requireNonNull(moduleResource);
        _dataType = ImportDataType.moduleResource;
        _dataValue = moduleResource;
        _module = module;
    }

    /**
     * Uploads a file for import.
     * @see #setUseAsync(boolean)
     * @see #setSaveToPipeline(boolean)
     */
    public void setFile(File file)
    {
        Objects.requireNonNull(file);
        _dataType = ImportDataType.file;
        _dataValue = file;
    }

    public Boolean isImportIdentity()
    {
        return _importIdentity;
    }

    public void setImportIdentity(Boolean importIdentity)
    {
        _importIdentity = importIdentity;
    }

    public Boolean isImportLookupByAlternateKey()
    {
        return _importLookupByAlternateKey;
    }

    public void setImportLookupByAlternateKey(Boolean importLookupByAlternateKey)
    {
        _importLookupByAlternateKey = importLookupByAlternateKey;
    }

    public String getFormat()
    {
        return _format;
    }

    public void setFormat(String format)
    {
        _format = format;
    }

    public InsertOption getInsertOption()
    {
        return _insertOption;
    }

    public void setInsertOption(InsertOption insertOption)
    {
        _insertOption = insertOption;
    }

    public boolean isUseAsync()
    {
        return _useAsync;
    }

    /**
     * Import the data in a pipeline job.
     * Currently only supported when submitting data as a file.
     * @see #setFile(File)
     */
    public void setUseAsync(boolean useAsync)
    {
        _useAsync = useAsync;
    }

    public boolean isSaveToPipeline()
    {
        return _saveToPipeline;
    }

    /**
     * Save the uploaded file to the pipeline root under the "QueryImportFiles" directory.
     * Currently only supported when submitting data as a file.
     * @see #setFile(File)
     */
    public void setSaveToPipeline(boolean saveToPipeline)
    {
        _saveToPipeline = saveToPipeline;
    }

    @Override
    public JSONObject getJsonObject()
    {
        throw new IllegalStateException();
    }

    @Override
    protected HttpUriRequestBase createRequest(URI uri)
    {
        Objects.requireNonNull(_schemaName, "schemaName required");
        Objects.requireNonNull(_queryName, "queryName required");

        Objects.requireNonNull(_dataType, "Data type required and may be one of 'text', 'path', 'moduleResource', or 'file'");
        Objects.requireNonNull(_dataValue, "Value for '" + _dataType + "' must not be null");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody("schemaName", _schemaName);
        builder.addTextBody("queryName", _queryName);
        if (_importIdentity != null)
            builder.addTextBody("importIdentity", Boolean.toString(_importIdentity));
        if (_importLookupByAlternateKey != null)
            builder.addTextBody("importLookupByAlternateKey", Boolean.toString(_importLookupByAlternateKey));
        if (_module != null)
            builder.addTextBody("module", _module);
        if (_dataType == ImportDataType.file)
            builder.addBinaryBody(_dataType.name(), (File)_dataValue, ContentType.APPLICATION_OCTET_STREAM, ((File)_dataValue).getName());
        else
            builder.addTextBody(_dataType.name(), (String)_dataValue);

        if (_format != null)
            builder.addTextBody("format", _format);

        if (_insertOption != null)
            builder.addTextBody("insertOption", _insertOption.toString());

        if (_saveToPipeline != null)
            builder.addTextBody("saveToPipeline", _saveToPipeline.toString());

        if (_useAsync != null)
            builder.addTextBody("useAsync", _useAsync.toString());

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

}
