package org.labkey.remoteapi.ms2;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

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
    protected HttpMethod createMethod()
    {
        return new PostMethod();
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
        return new StartSearchResponse(text, status, contentType, json, this);
    }
}
