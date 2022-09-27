/*
 * Copyright (c) 2017-2018 LabKey Corporation
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
import org.labkey.remoteapi.CommandResponse;

import java.util.*;


public class LineageResponse extends CommandResponse
{
    List<LineageNode> _seeds;
    Map<String, LineageNode> _nodes;

    public LineageResponse(String text, int statusCode, String contentType, JSONObject json, LineageCommand sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);

        List<String> seedLsids = getProperty("seeds");
        List<LineageNode> seeds = new ArrayList<>(seedLsids.size());

        Map<String, Object> nodeMap = getProperty("nodes");

        Map<String, LineageNode> nodes = new HashMap<>();

        for (Map.Entry<String, Object> entry : nodeMap.entrySet())
        {
            String lsid = entry.getKey();
            LineageNode node = new LineageNode(lsid, (Map<String, Object>)entry.getValue());
            nodes.put(lsid, node);

            if (seeds.size() != seedLsids.size() && seedLsids.contains(lsid))
                seeds.add(node);
        }

        for (LineageNode node : nodes.values())
            node.fixup(nodes);

        _seeds = Collections.unmodifiableList(seeds);
        _nodes = Collections.unmodifiableMap(nodes);
    }

    public LineageNode getSeed()
    {
        if (_seeds.size() > 0)
            return _seeds.get(0);
        return null;
    }

    public List<LineageNode> getSeeds()
    {
        return _seeds;
    }

    public Map<String, LineageNode> getNodes()
    {
        return _nodes;
    }

    public String dump()
    {
        StringBuilder sb = new StringBuilder();
        dump(sb);
        return sb.toString();
    }

    private void dump(StringBuilder sb)
    {
        if (_seeds == null)
        {
            sb.append("No seeds found");
            return;
        }

        Set<String> seen = new HashSet<>();
        for (LineageNode seed : _seeds)
        {
            seed.dump(0, sb, seen);
        }
    }

}
