/*
 * Copyright (c) 2011-2014 LabKey Corporation
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
package org.labkey.remoteapi.ms2;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Initiates an MS2 search.
 * User: jeckels
 * Date: Feb 11, 2011
 */
public class StartSearchCommand extends Command<StartSearchResponse>
{
    private String _protocol;
    private String _path;
    private List<String> _files;
    private final SearchEngine _searchEngine;

    /** The set of supported search engines, which may or may not be available on any given server */
    public enum SearchEngine { XTandem, Sequest, Mascot }

    /**
     * @param searchEngine the search engine to be used for the analysis
     * @param protocol the name of the search protocol that has already been defined on the server that should be used
     * @param path the path relative to the pipeline root for the target container, in which the files will be present
     * @param files the list of file names in the path specified that should be part of the search
     */
    public StartSearchCommand(SearchEngine searchEngine, String protocol, String path, List<String> files)
    {
        super("ms2-pipeline", "search" + searchEngine.name());
        _searchEngine = searchEngine;
        setProtocol(protocol);
        setPath(path);
        setFiles(files);
    }

    /** @return the name of the search protocol that has already been defined on the server that should be used */
    public String getProtocol()
    {
        return _protocol;
    }

    /** @param protocol the name of the search protocol that has already been defined on the server that should be used */
    public void setProtocol(String protocol)
    {
        _protocol = protocol;
    }

    /** @return the path relative to the pipeline root for the target container, in which the files will be present */
    public String getPath()
    {
        return _path;
    }

    /** @param path the path relative to the pipeline root for the target container, in which the files will be present */
    public void setPath(String path)
    {
        _path = path;
    }

    /** @return the list of file names in the path specified that should be part of the search */
    public List<String> getFiles()
    {
        return _files;
    }

    /** @param files the list of file names in the path specified that should be part of the search */
    public void setFiles(List<String> files)
    {
        _files = files;
    }

    @Override
    protected HttpUriRequest createRequest(URI uri)
    {
        return new HttpPost(uri);
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("path", _path);
        result.put("file", _files);
        result.put("protocol", _protocol);
        result.put("runSearch", true);
        return result;
    }

    @Override
    protected StartSearchResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        if (text.startsWith("ERROR="))
        {
            throw new IllegalArgumentException(text.substring("ERROR=".length()));
        }
        return new StartSearchResponse(text, status, contentType, json, this);
    }
}
