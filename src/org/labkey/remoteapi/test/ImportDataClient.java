package org.labkey.remoteapi.test;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.query.ImportDataCommand;
import org.labkey.remoteapi.query.ImportDataResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

class ImportDataClient
{

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
        ImportDataCommand.InsertOption insertOption = null;
        Boolean importIdentity = null;
        Boolean importLookupByAlternateKey = null;

        if (args.length < 5)
        {
            printUsage();
            return;
        }

        OUTER:
        for (int i = 0; i < args.length; i++)
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
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument: 'csv' or 'tsv'");
                    format = args[++i];
                    continue OUTER;

                case "-x":
                case "--insertOption":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument: " + Arrays.stream(ImportDataCommand.InsertOption.values()).map(Objects::toString).collect(Collectors.joining(", ")));
                    insertOption = ImportDataCommand.InsertOption.valueOf(args[++i]);
                    continue OUTER;

                case "-U":
                case "--username":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    user = args[++i];
                    continue OUTER;

                case "-P":
                case "--password":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    password = args[++i];
                    continue OUTER;

                case "-f":
                case "--file":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    localPath = args[++i];
                    continue OUTER;

                case "-p":
                case "--path":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    remotePath = args[++i];
                    continue OUTER;

                case "-r":
                case "--resource":
                    if (i == args.length - 1)
                        throw new IllegalArgumentException(arg + " requires argument");
                    moduleResource = args[++i];
                    continue OUTER;

                case "-t":
                case "--text":
                    if (i == args.length - 1)
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
            String resourcePath = moduleResource.substring(colon + 1);
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
        System.err.println("  -x, --insertOption [" + Arrays.stream(ImportDataCommand.InsertOption.values()).map(Objects::toString).collect(Collectors.joining("|")) + "]");
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
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset())))
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
