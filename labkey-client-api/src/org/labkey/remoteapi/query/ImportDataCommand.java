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
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Import data in bulk from a text, local file, or a file
 * contained within a module on the LabKey Server.
 *
 * User: kevink
 * Date: 2/3/17
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
    protected HttpUriRequest createRequest(URI uri)
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

    @Override
    public ImportDataCommand copy()
    {
        return new ImportDataCommand(this);
    }

    public static void main(String[] args) throws IOException
    {
        // required
        String baseServerUrl = null;
        String folderPath = null;
        String schemaName = null;
        String queryName = null;

        // data
        String localPath = null;
        String remotePath = null;
        String moduleResource = null;
        String text = null;

        // optional
        String user = null;
        String password = null;
        String format = null;
        Boolean useAsync = null;
        Boolean saveToPipeline = null;
        InsertOption insertOption = null;
        Boolean importIdentity = null;
        Boolean importLookupByAlternateKey = null;

        if (args.length < 5)
        {
            printUsage();
            return;
        }

        OUTER: for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            //
            // optional flag arguments
            //

            switch (arg)
            {
                case "-h":
                case "--help":
                    printUsage();
                    return;

                case "-d":
                case "--importIdentity":
                    importIdentity = true;
                    continue OUTER;

                case "-k":
                case "--importLookupByAlternateKey":
                    importLookupByAlternateKey = true;
                    continue OUTER;

                case "-a":
                case "--async":
                    useAsync = true;
                    continue OUTER;

                case "-s":
                case "--saveToPipeline":
                    saveToPipeline = true;
                    continue OUTER;

                case "-F":
                case "--format":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument: 'csv' or 'tsv'");
                    format = args[++i];
                    continue OUTER;

                case "-x":
                case "--insertOption":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument: " + Arrays.stream(InsertOption.values()).map(Objects::toString).collect(Collectors.joining(", ")));
                    insertOption = InsertOption.valueOf(args[++i]);
                    continue OUTER;

                case "-U":
                case "--username":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    user = args[++i];
                    continue OUTER;

                case "-P":
                case "--password":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    password = args[++i];
                    continue OUTER;

                case "-f":
                case "--file":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    localPath = args[++i];
                    continue OUTER;

                case "-p":
                case "--path":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    remotePath = args[++i];
                    continue OUTER;

                case "-r":
                case "--resource":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    moduleResource = args[++i];
                    continue OUTER;

                case "-t":
                case "--text":
                    if (i == args.length-1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    text = args[++i];
                    continue OUTER;
            }

            //
            // positional arguments
            //

            if (baseServerUrl == null)
                baseServerUrl = arg;
            else if (folderPath == null)
                folderPath = arg;
            else if (schemaName == null)
                schemaName = arg;
            else if (queryName == null)
                queryName = arg;
            else
            {
                System.err.println("Unexpected argument '" + arg + "'. See --help for more.");
                return;
            }
        }

        if (baseServerUrl == null)
        {
            System.err.println("Server base URL required. See --help for more");
            return;
        }

        if (folderPath == null)
        {
            System.err.println("Server folder required. See --help for more");
            return;
        }

        if (schemaName == null)
        {
            System.err.println("Schema name required. See --help for more");
            return;
        }

        if (queryName == null)
        {
            System.err.println("Query name required. See --help for more");
            return;
        }

        if (localPath == null && remotePath == null && moduleResource == null && text == null)
        {
            System.err.println("One of --file, --path, --resource, --text is required. See --help for more");
            return;
        }

        ImportDataCommand cmd = new ImportDataCommand(schemaName, queryName);

        if (localPath != null)
        {
            cmd.setFile(new File(localPath));
        }
        else if (remotePath != null)
        {
            cmd.setPath(remotePath);
        }
        else if (moduleResource != null)
        {
            int colon = moduleResource.indexOf(":");
            if (colon == -1)
            {
                System.err.println("Expected module resource: <module>:<resourcePath>");
                return;
            }
            String module = moduleResource.substring(0, colon);
            String resourcePath = moduleResource.substring(colon+1);
            cmd.setModuleResource(module, resourcePath);
        }
        else if (text != null)
        {
            if (text.equals("-"))
            {
                text = readFully(System.in);
            }
            else
            {
                text = readFully(new FileInputStream(text));
            }
            cmd.setText(text);
        }

        if (importIdentity != null)
            cmd.setImportIdentity(importIdentity);

        if (importLookupByAlternateKey != null)
            cmd.setImportLookupByAlternateKey(importLookupByAlternateKey);

        if (format != null)
            cmd.setFormat(format);

        if (insertOption != null)
            cmd.setInsertOption(insertOption);

        if (useAsync != null)
            cmd.setUseAsync(useAsync);

        if (saveToPipeline != null)
            cmd.setSaveToPipeline(saveToPipeline);

        Connection conn;
        if (user != null && password != null)
            conn = new Connection(baseServerUrl, user, password);
        else
            conn = new Connection(baseServerUrl);

        try
        {
            ImportDataResponse resp = cmd.execute(conn, folderPath);
            if (resp.getSuccess())
                System.out.println("Successfully imported " + resp.getRowCount() + " rows");
            else
                System.out.println("Failed to import data");// error message? {error: {_form: '...'}}
        }
        catch (CommandException e)
        {
            System.err.println("Failure! Response code: " + e.getStatusCode());
            e.printStackTrace();
            System.err.println();
            String responseText = e.getResponseText();
            if (responseText != null)
            {
                System.err.println("Response text: ");
                System.err.println(responseText);
            }
        }
    }

    private static void printUsage()
    {
        System.err.println("Usage:");
        System.err.println("  java " + ImportDataCommand.class.getName() + " [OPTIONS] SERVER_URL FOLDER_PATH SCHEMA QUERY");
        System.err.println();
        System.err.println("Requires one of:");
        System.err.println("  -f, --file LOCAL-PATH");
        System.err.println("  -p, --path SERVER-FILE-PATH");
        System.err.println("  -r, --resource MODULE:MODULE-RESOURCE-PATH");
        System.err.println("  -t, --text LOCAL-PATH (use '-' to read from stdin)");
        System.err.println();
        System.err.println("Other options:");
        System.err.println("  -U, --username USERNAME");
        System.err.println("  -P, --password PASSWORD");
        System.err.println("  -d, --importIdentity");
        System.err.println("  -k, --importLookupByAlternateKey");
        System.err.println("  -x, --insertOption [" + Arrays.stream(InsertOption.values()).map(Objects::toString).collect(Collectors.joining("|")) + "]");
        System.err.println("  -F, --format [tsv|csv] (may be used with --text))");
        System.err.println("  -a, --async (may be used with --file)");
        System.err.println("  -s, --saveToPipeline (may be used with --file)");
        System.err.println();
        System.err.println("Examples");
        System.err.println("  java " + ImportDataCommand.class.getName() + " -a -p --file file.tsv http://localhost:8080/labkey /foldername lists MyList");
        System.err.println();
        System.err.println("  java " + ImportDataCommand.class.getName() + " --resource biologics:data/test/lists/Vessel.tsv http://localhost:8080/labkey /foldername lists Vessels");
        System.err.println();
    }

    private static String readFully(InputStream in) throws IOException
    {
        StringWriter sw = new StringWriter();
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(in)))
        {
            String line;
            do
            {
                line = buf.readLine();
                if (line != null)
                    sw.append(line).append(System.lineSeparator());
            }
            while (line != null);
        }

        return sw.toString();
    }
}
