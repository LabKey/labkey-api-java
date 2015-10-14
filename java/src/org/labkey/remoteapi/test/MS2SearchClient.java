/*
 * Copyright (c) 2011-2015 LabKey Corporation
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
package org.labkey.remoteapi.test;

import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.util.*;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.assay.*;
import org.labkey.remoteapi.ms2.StartSearchCommand;

/**
 * Demo code that accepts one or more CSV files on the command line. It creates assay runs to hold the metadata
 * from the CSV, and then initiates MS2 searches on the files. If the files are not yet available, the jobs will block
 * until the server is notified that files are available (see PipelineFileAvailableClient).
 *
 * Expects to receive the path to one or more CSV files on the command line. Each CSV is required to have the following
 * columns:
 *
 * FileName: the name of the mzXML or RAW file to be searched
 * Path: the path to the file
 * Sample: the name of the sample. Used to determine if files are fractions or independent samples.
 * LabKeyFolder: the target folder in LabKey Server
 * ProtocolToRun: the name of the existing search protocol to be used when searching the files
 *
 * If other columns are present in the CSV, they will be associated with the assay runs as run properties if the assay
 * design includes run properties with the same names.
 *
 * By default looks for a config.properties in the working directory, but the location can be specified with a
 * -config=&lt;PATH TO CONFIG FILE&gt; argument.
 *
 * Expects the following config properties:
 * baseServerURL: base URL of the LabKey Server, such as "http://www.labkey.org"
 * username: credentials for logging in
 * password: credentials for logging in
 * pipelineRoot: the path to the pipeline root for the target folder. Used to pass a relative path to the server.  
 * assayId: the rowId of the GPAT assay design to be used for storing metadata
 * searchEngine: the name of the MS2 search engine to use. Supported values are XTandem, Sequest, and Mascot. 
 * debug: (Optional) If true, print verbose HTTP connection debugging information
 */
public class MS2SearchClient
{
    /** Simple bean class to hold the values that uniquely identify the files to be submitted in one search */
    private static class SearchInfo
    {
        private String _sample;
        private String _protocol;
        private String _folder;
        private String _path;

        private SearchInfo(String sample, String protocol, String folder, String path)
        {
            _sample = sample;
            _protocol = protocol;
            _folder = folder;
            _path = path;
        }

        public String getProtocol()
        {
            return _protocol;
        }

        public String getFolder()
        {
            return _folder;
        }

        public String getPath()
        {
            return _path;
        }

        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (!(o instanceof SearchInfo))
                return false;
            SearchInfo that = (SearchInfo) o;
            if (_folder == null ? that._folder != null : !_folder.equals(that._folder))
                return false;
            if (_path == null ? that._path != null : !_path.equals(that._path))
                return false;
            if (_protocol == null ? that._protocol != null : !_protocol.equals(that._protocol))
                return false;
            return _sample == null ? that._sample == null : _sample.equals(that._sample);
        }

        public int hashCode()
        {
            int result = _sample == null ? 0 : _sample.hashCode();
            result = 31 * result + (_protocol == null ? 0 : _protocol.hashCode());
            result = 31 * result + (_folder == null ? 0 : _folder.hashCode());
            result = 31 * result + (_path == null ? 0 : _path.hashCode());
            return result;
        }

    }
    
    public static void main(String... args) throws Exception
    {
        ClientConfig config = new ClientConfig(args);

        int assayId = getAssayId(config);

        // Map from target folder path to assay batch
        Map<String, Batch> batches = new HashMap<String, Batch>();

        // Map from MS2 search info to the list of file names to be searched
        Map<SearchInfo, List<String>> searches = new HashMap<SearchInfo, List<String>>();

        parseCSVs(args, batches, searches);

        Connection connection = config.createConnection();
        try
        {
            submitAssayBatches(assayId, batches, connection);
            submitSearches(config, searches, connection);
        }
        catch (CommandException e)
        {
            System.err.println((new StringBuilder()).append("Failure! Response code: ").append(e.getStatusCode()).toString());
            e.printStackTrace();
            System.err.println();
            if (e.getResponseText() != null)
            {
                System.err.println("Response text: ");
                System.err.println(e.getResponseText());
            }
            System.exit(1);
        }
    }

    /**
     * Initiates MS2 searches 
     */
    private static void submitSearches(ClientConfig config, Map<SearchInfo, List<String>> searches, Connection connection)
            throws IOException, CommandException
    {
        String pipelineRoot = config.getProperty("pipelineRoot");
        String searchEngineName = config.getProperty("searchEngine");

        StartSearchCommand.SearchEngine searchEngine = null;

        for (StartSearchCommand.SearchEngine engine : StartSearchCommand.SearchEngine.values())
        {
            if (engine.name().equalsIgnoreCase(searchEngineName))
            {
                searchEngine = engine;
            }
        }

        if (searchEngine == null)
        {
            throw new IllegalArgumentException("searchEngine '" + searchEngineName + "' is not one of the available options: " + Arrays.asList(StartSearchCommand.SearchEngine.values()));
        }

        for (Map.Entry<SearchInfo, List<String>> entry : searches.entrySet())
        {
            SearchInfo searchInfo = entry.getKey();

            // Figure out a relative path from the pipeline root
            String fullPath = searchInfo.getPath();
            if (!fullPath.toLowerCase().startsWith(pipelineRoot.toLowerCase()))
            {
                throw new IllegalArgumentException((new StringBuilder()).append("Could not determine relative path for '").append(fullPath).toString());
            }
            String relativePath = fullPath.substring(pipelineRoot.length());
            relativePath = relativePath.replace('\\', '/');
            if (!relativePath.startsWith("/"))
            {
                relativePath = "/" + relativePath;
            }
            List<String> fileNames = entry.getValue();

            System.out.print("Submitting search using protocol " + searchInfo.getProtocol() + " to " + searchInfo.getFolder() + ", with " + fileNames.size() + " files");
            StartSearchCommand startCommand = new StartSearchCommand(searchEngine, searchInfo.getProtocol(), relativePath, fileNames);
            startCommand.execute(connection, searchInfo.getFolder());
        }
    }

    /**
     * Iterate over all of the batch objects and submit them to the right target folder
     *
     * @param assayId rowId of the target assay design
     * @param batches a map from target folder to assay batch
     */
    private static void submitAssayBatches(int assayId, Map<String, Batch> batches, Connection connection)
            throws IOException, CommandException
    {
        for (Map.Entry<String, Batch> entry : batches.entrySet())
        {
            Batch batch = entry.getValue();
            String targetFolder = entry.getKey();
            SaveAssayBatchCommand command = new SaveAssayBatchCommand(assayId, batch);
            System.out.print("Submitting metadata from " + batch + " to " + targetFolder + ", describing " + batch.getRuns().size() + " files");
            command.execute(connection, targetFolder);
        }
    }

    /**
     * Parse all of the CSVs passed on the command line and build up the list of assay batches to be inserted,
     * and the searches to be initiated.
     */
    private static void parseCSVs(String[] args, Map<String, Batch> batches, Map<SearchInfo, List<String>> searches)
            throws IOException
    {
        for (String arg : args)
        {
            // Skip -config=<PATH> or other non-CSV arguments
            if (!arg.startsWith("-"))
            {
                File csvFile = new File(arg);
                System.out.println("Processing " + csvFile);
                List<String[]> lines;
                InputStream in = new FileInputStream(csvFile);
                try
                {
                    // Parse the CSVs into lists of string values
                    Reader reader = new InputStreamReader(in);
                    CSVReader csvReader = new CSVReader(reader, ',', '"', '~');
                    lines = csvReader.readAll();
                }
                finally
                {
                    in.close();
                }

                if (lines.size() < 2)
                {
                    throw new IllegalArgumentException("Expected to have at least two rows in CSV, one with headers and others with data in " + csvFile.getPath());
                }

                String headers[] = lines.get(0);
                // Look at each data row within the CSV
                for (int i = 1; i < lines.size(); i++)
                {
                    String[] line = lines.get(i);

                    // Skip lines that are completely empty
                    boolean blankLink = true;
                    for (String value : line)
                    {
                        if (value != null && !value.trim().isEmpty())
                        {
                            blankLink = false;
                            break;
                        }
                    }
                    if (blankLink)
                    {
                        continue;
                    }

                    // Pull out the required values
                    String fileName = getValue(line, headers, "FileName", i);
                    String path = getValue(line, headers, "Path", i);
                    String sample = getValue(line, headers, "Sample", i);
                    String folder = getValue(line, headers, "LabKeyFolder", i);
                    String protocol = getValue(line, headers, "ProtocolToRun", i);

                    // Create a map of all the properties. If the assay design has run properties with names that
                    // match, the values will be stored with the run.
                    Map<String, String> runProperties = new HashMap<String, String>();
                    for (int columnIndex = 0; columnIndex < line.length; columnIndex++)
                    {
                        runProperties.put(headers[columnIndex], line[columnIndex]);
                    }

                    // We want to create one batch per folder, so grab an existing batch if we have one
                    Batch batch = batches.get(folder);
                    if (batch == null)
                    {
                        // This is the first run for this folder, so create a new batch
                        batch = new Batch();
                        batch.setName(csvFile.getName());
                        batches.put(folder, batch);
                    }

                    addRunToBatch(batch, fileName, path, runProperties);

                    SearchInfo info = new SearchInfo(sample, protocol, folder, path);
                    List<String> files = searches.get(info);
                    if (files == null)
                    {
                        files = new ArrayList<String>();
                        searches.put(info, files);
                    }
                    files.add(fileName);
                }
            }
        }
    }

    /** @return the parsed assayId config property as an int */
    private static int getAssayId(ClientConfig config)
    {
        String assayIdString = config.getProperty("assayId");
        int assayId;
        try
        {
            assayId = Integer.parseInt(assayIdString);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("Could not parse assayId: ").append(assayIdString).toString(), e);
        }
        return assayId;
    }

    /**
     * @return the value for the given column in the specified line.
     * @param lineNumber used only to build up an error message if the value can't be found
     */
    private static String getValue(String line[], String headers[], String columnName, int lineNumber)
    {
        int columnIndex = findIndex(headers, columnName);
        if (columnIndex >= line.length)
            throw new IllegalArgumentException((new StringBuilder()).append("Row ").append(lineNumber).append(" is incomplete in CSV file. It contains fewer columns than the header does.").toString());
        String result = line[columnIndex];
        if (result == null || result.trim().equals(""))
            throw new IllegalArgumentException((new StringBuilder()).append("Row ").append(lineNumber).append(" is incomplete in CSV file, it does not include a value for '").append(columnName).append("'").toString());
        else
            return result.trim();
    }

    /** Adds a new run to the assay batch to store the metadata for the current input MS2 file */
    private static void addRunToBatch(Batch batch, String fileName, String path, Map<String, String> runProperties)
    {
        Run run = new Run();
        run.setName(fileName);
        run.getProperties().putAll(runProperties);
        batch.getRuns().add(run);
        List<Data> outputFiles = new ArrayList<Data>();
        Data data = new Data();
        data.setAbsolutePath(path + File.separator + fileName);
        outputFiles.add(data);
        run.setDataOutputs(outputFiles);
    }

    /** Finds a column by name given the column headers */
    private static int findIndex(String headers[], String propertyName)
    {
        for (int i = 0; i < headers.length; i++)
        {
            if (propertyName.equalsIgnoreCase(headers[i]))
                return i;
        }

        throw new IllegalArgumentException("Unable to find required property '" + propertyName + "' in input CSV file");
    }
}
