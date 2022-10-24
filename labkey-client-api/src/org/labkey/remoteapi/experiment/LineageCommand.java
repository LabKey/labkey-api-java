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
package org.labkey.remoteapi.experiment;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.Connection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LineageCommand extends Command<LineageResponse>
{
    // One or more LSIDs are required
    private final List<String> _lsids;

    // Optional parameters below
    private final Boolean _parents;
    private final Boolean _children;
    private final Integer _depth;
    private final String _expType;
    private final String _cpasType;
    private final Boolean _includeProperties;
    private final Boolean _includeInputsAndOutputs;
    private final Boolean _includeRunSteps;

    private LineageCommand(
            List<String> lsids, Boolean parents, Boolean children, Integer depth, String cpasType, String expType,
            Boolean includeProperites, Boolean includeInputsAndOutputs, Boolean includeRunSteps)
    {
        super("experiment", "lineage");
        if (lsids == null || lsids.isEmpty())
            throw new IllegalArgumentException("One or more starting LSIDs required");

        _lsids = lsids;
        _depth = depth;
        _parents = parents;
        _children = children;
        _expType = expType;
        _cpasType = cpasType;
        _includeProperties = includeProperites;
        _includeInputsAndOutputs = includeInputsAndOutputs;
        _includeRunSteps = includeRunSteps;
    }

    public static final class Builder
    {
        private final List<String> _lsids;

        private Integer _depth;
        private Boolean _parents;
        private Boolean _children;
        private String _expType;
        private String _cpasType;
        private Boolean _includeProperties;
        private Boolean _includeInputsAndOutputs;
        private Boolean _includeRunSteps;

        public Builder(String lsid)
        {
            this(Arrays.asList(lsid));
        }

        public Builder(List<String> lsids)
        {
            if (lsids == null || lsids.isEmpty())
                throw new IllegalArgumentException("One or more starting LSIDs required");

            _lsids = lsids;
        }

        public Builder setDepth(Integer depth)
        {
            _depth = depth;
            return this;
        }

        public Builder setParents(Boolean parents)
        {
            _parents = parents;
            return this;
        }

        public Builder setChildren(Boolean children)
        {
            _children = children;
            return this;
        }

        public Builder setExpType(String expType)
        {
            _expType = expType;
            return this;
        }

        public Builder setCpasType(String cpasType)
        {
            _cpasType = cpasType;
            return this;
        }

        public Builder setIncludeProperties(Boolean includeProperties)
        {
            _includeProperties = includeProperties;
            return this;
        }

        public Builder setIncludeInputsAndOutputs(Boolean includeInputsAndOutputs)
        {
            _includeInputsAndOutputs = includeInputsAndOutputs;
            return this;
        }

        public Builder setIncludeRunSteps(Boolean includeRunSteps)
        {
            _includeRunSteps = includeRunSteps;
            return this;
        }

        public LineageCommand build()
        {
            return new LineageCommand(
                    _lsids, _parents, _children, _depth, _cpasType, _expType,
                    _includeProperties, _includeInputsAndOutputs, _includeRunSteps);
        }
    }

    @Override
    protected LineageResponse createResponse(String text, int statusCode, String contentType, JSONObject json)
    {
        return new LineageResponse(text, statusCode, contentType, json, this);
    }

    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<>();
        params.put("lsids", _lsids);
        if (null != _parents)
            params.put("parents", _parents);
        if (null != _children)
            params.put("children", _children);
        if (null != _depth)
            params.put("depth", _depth);
        if (null != _expType)
            params.put("expType", _expType);
        if (null != _cpasType)
            params.put("cpasType", _cpasType);
        if (null != _includeProperties)
            params.put("includeProperties", _includeProperties);
        if (null != _includeInputsAndOutputs)
            params.put("includeInputsAndOutputs", _includeInputsAndOutputs);
        if (null != _includeRunSteps)
            params.put("includeRunSteps", _includeRunSteps);

        return params;
    }


    @Override
    public LineageCommand copy()
    {
        return new LineageCommand(_lsids, _parents, _children, _depth, _cpasType, _expType,
                _includeProperties, _includeInputsAndOutputs, _includeRunSteps);
    }

    public static void main(String[] args) throws Exception
    {
        String url = "http://localhost:8080/labkey";
        String folderPath = "/AssayImportProvenance Test";
        String user = "kevink@labkey.com";
        String password = "xxxxxx";

        String lsid = "urn:lsid:labkey.com:GeneralAssayRun.Folder-4780:40166791-3d3f-1039-a854-9b7575483a25";

        LineageCommand cmd = new Builder(lsid)
                .setParents(false)
                .setChildren(true)
                .setIncludeProperties(true)
                .setIncludeInputsAndOutputs(true)
                .setIncludeRunSteps(true)
                .build();

        Connection conn = new Connection(url, user, password);
        LineageResponse resp = cmd.execute(conn, folderPath);
        System.out.println(resp.dump());
    }
}
