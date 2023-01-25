/*
 * Copyright (c) 2008-2019 LabKey Corporation
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

import org.json.JSONObject;
import org.labkey.remoteapi.PostCommand;

import java.util.Map;

/**
 * Command for obtaining information about the current assay definitions
 * in a particular folder.
 * <p>
 * By default, this command returns information about all assays, but
 * you may use the various setters to filter this list to assays of a given
 * name, type or id.
 */
public class AssayListCommand extends PostCommand<AssayListResponse>
{
    private String _name;
    private String _type;
    private Integer _id;

    /**
     * Constructs a new AssayListCommand object.
     */
    public AssayListCommand()
    {
        super("assay", "assayList");
    }

    /**
     * Returns the assay name filter upon (if any)
     * @return The current assay name filter, or null if not filter has been set.
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the assay name to filter upon. Use this to get information about
     * a particular assay identified by the supplied name.
     * @param name The assay name.
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the current assay type filter (if any).
     * @return The current assay type filter or null if not filter has been set.
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Sets the assay type filter. For example, to get information
     * on Luminex assays only, set this to 'Luminex'.
     * @param type The type to filter upon.
     */
    public void setType(String type)
    {
        _type = type;
    }

    /**
     * Returns the current assay id filter (if any).
     * @return The current assay id filter, or null if none has been set.
     */
    public Integer getId()
    {
        return _id;
    }

    /**
     * Sets the current assay id filter. Set this to a valid id to get information about
     * that assay only, or null to get information about all assays.
     * @param id The assay id.
     */
    public void setId(Integer id)
    {
        _id = id;
    }

    @Override
    protected AssayListResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new AssayListResponse(text, status, contentType, json);
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject obj = new JSONObject();

        obj.put("name", getName());
        obj.put("type", getType());
        obj.put("id", getId());

        return obj;
    }

    @Override
    protected Map<String, Object> createParameterMap()
    {
        Map<String, Object> params = super.createParameterMap();

        if (null != getName())
            params.put("name", getName());
        if (null != getType())
            params.put("type", getType());
        if (null != getId())
            params.put("id", getId());

        return params;
    }
}
