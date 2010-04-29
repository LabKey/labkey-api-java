package org.labkey.remoteapi.assay;

import org.json.simple.JSONObject;

/**
* Represents a data file that may be used in a run. In order to successfully look up a data file when saving a run,
* the server checks for the id, pipelinePath, dataFileURL, and pipelinePath properties (in that order) to try to resolve
* the file reference. At least one must be specified.  
* User: jeckels
* Date: Apr 28, 2010
*/
public class Data extends ExpObject
{
    private String _dataFileURL;
    private String _absolutePath;
    private String _pipelinePath;

    public Data()
    {
        super();
    }

    public Data(JSONObject json)
    {
        super(json);
        _dataFileURL = (String)json.get("dataFileURL");
        _absolutePath = (String)json.get("absolutePath");
        _pipelinePath = (String)json.get("pipelinePath");
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();
        result.put("dataFileURL", _dataFileURL);
        result.put("absolutePath", _absolutePath);
        result.put("pipelinePath", _pipelinePath);
        return result;
    }

    /** @return the URL to the data file from the web server's perspective */
    public String getDataFileURL()
    {
        return _dataFileURL;
    }

    /** @param dataFileURL the URL to the data file from the web server's perspective */
    public void setDataFileURL(String dataFileURL)
    {
        _dataFileURL = dataFileURL;
    }

    /** @return the full path to the file from the web server's file system perspective */
    public String getAbsolutePath()
    {
        return _absolutePath;
    }

    /** @param absolutePath the full path to the file from the web server's file system perspective */
    public void setAbsolutePath(String absolutePath)
    {
        _absolutePath = absolutePath;
    }

    /** @return the path to the file on the file system, relative to the folder's pipeline root */
    public String getPipelinePath()
    {
        return _pipelinePath;
    }

    /** @param pipelinePath the path to the file on the file system, relative to the folder's pipeline root */
    public void setPipelinePath(String pipelinePath)
    {
        _pipelinePath = pipelinePath;
    }
}
