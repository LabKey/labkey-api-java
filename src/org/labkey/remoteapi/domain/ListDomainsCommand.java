package org.labkey.remoteapi.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.Set;

public class ListDomainsCommand extends PostCommand<ListDomainsResponse>
{
    private boolean _includeFields;
    private boolean _includeProjectAndShared;
    private Set<String> _domainKinds;
    private String _containerPath;

    public ListDomainsCommand(boolean includeFields, boolean includeProjectAndShared, Set<String> domainKinds, String containerPath)
    {
        super("property", "listDomains");
        _includeFields = includeFields;
        _includeProjectAndShared = includeProjectAndShared;
        _domainKinds = domainKinds;
        _containerPath = containerPath;
    }

    public boolean isIncludeFields()
    {
        return _includeFields;
    }

    public void setIncludeFields(boolean includeFields)
    {
        _includeFields = includeFields;
    }

    public boolean isIncludeProjectAndShared()
    {
        return _includeProjectAndShared;
    }

    public void setIncludeProjectAndShared(boolean includeProjectAndShared)
    {
        _includeProjectAndShared = includeProjectAndShared;
    }

    public Set<String> getDomainKinds()
    {
        return _domainKinds;
    }

    public void setDomainKinds(Set<String> domainKinds)
    {
        _domainKinds = domainKinds;
    }

    public String getContainerPath()
    {
        return _containerPath;
    }

    public void setContainerPath(String containerPath)
    {
        _containerPath = containerPath;
    }

    @Override
    protected ListDomainsResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new ListDomainsResponse(text, status, contentType, json);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject result = new JSONObject();

        result.put("containerPath", _containerPath);
        result.put("domainKinds", new JSONArray(getDomainKinds()));
        result.put("includeFields", _includeFields);
        result.put("includeProjectAndShared", _includeProjectAndShared);


        return result;
    }
}
