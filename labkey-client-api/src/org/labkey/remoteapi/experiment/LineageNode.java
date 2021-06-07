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


import org.apache.commons.logging.LogFactory;
import org.labkey.remoteapi.ResponseObject;
import org.labkey.remoteapi.query.DateParser;
import org.labkey.remoteapi.query.Filter;
import org.labkey.remoteapi.query.SelectRowsResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LineageNode extends ResponseObject
{
    private final String _lsid;
    private final Integer _id;
    private final String _name;
    private final String _container;
    private final String _url;
    private final String _type;
    private final String _cpasType;
    private final String _expType;
    private final Date _created;
    private final String _createdBy;
    private final Date _modified;
    private final String _modifiedBy;
    private final String _comment;

    // query reference
    private final String _schemaName;
    private final String _queryName;
    private final List<Filter> _pkFilters;

    private final Map<String, Object> _properties;

    private List<Edge> _parents;
    private List<Edge> _children;


    public LineageNode(String lsid, Map<String, Object> map)
    {
        super(map);
        _lsid = lsid;
        _id = ((Number)map.getOrDefault("id", -1)).intValue();
        _name = (String)map.get("name");
        _container = (String)map.get("container");
        _type = (String)map.get("type");
        _cpasType = (String)map.get("cpasType");
        _expType = (String)map.get("expType");
        _url = (String)map.get("url");

        Date created = null;
        Date modified = null;
        DateParser dateParser = new DateParser();
        try
        {
            String createdStr = (String) map.get("created");
            if (createdStr != null && createdStr.length() > 0)
                created = dateParser.parse(createdStr);
            String modifiedStr = (String) map.get("modified");
            if (modifiedStr != null && modifiedStr.length() > 0)
                modified = dateParser.parse(modifiedStr);
        }
        catch (ParseException e)
        {
            //just log it--if it doesn't parse, we can't fix it up
            LogFactory.getLog(SelectRowsResponse.class).warn("Failed to parse date: " + e.getMessage(), e);
        }
        _created = created;
        _modified = modified;

        _createdBy = (String) map.get("createdBy");
        _modifiedBy = (String)map.get("modifiedBy");
        _comment = (String)map.get("comment");

        // query row ref
        _schemaName = (String)map.get("schemaName");
        _queryName = (String)map.get("queryName");
        _pkFilters = createPkFilters((List<Map<String, Object>>)map.get("pkFilters"));

        _properties = (Map<String, Object>)map.getOrDefault("properties", Collections.emptyMap());
    }

    void fixup(Map<String, LineageNode> nodes)
    {
        _children = fixupEdges(nodes, (List<Map<String, Object>>) getAllProperties().get("children"));
        _parents = fixupEdges(nodes, (List<Map<String, Object>>) getAllProperties().get("parents"));
    }

    List<Filter> createPkFilters(List<Map<String, Object>> filters)
    {
        if (filters == null)
            return Collections.emptyList();

        List<Filter> result = new ArrayList<>(filters.size());
        for (Map<String, Object> filter : filters)
        {
            String fieldKey = (String)filter.get("fieldKey");
            Object value = filter.get("value");
            result.add(new Filter(fieldKey, value, Filter.Operator.EQUAL));
        }
        return Collections.unmodifiableList(result);
    }

    List<Edge> fixupEdges(Map<String, LineageNode> nodes, List<Map<String, Object>> edges)
    {
        List<Edge> fixedEdges = new ArrayList<>();
        for (Map<String, Object> edge : edges)
        {
            String role = (String)edge.get("role");
            String lsid = (String)edge.get("lsid");
            fixedEdges.add(new Edge(role, nodes.get(lsid)));
        }
        return fixedEdges;
    }

    public String getLsid()
    {
        return _lsid;
    }

    public Integer getId()
    {
        return _id;
    }

    public String getName()
    {
        return _name;
    }

    public String getUrl()
    {
        return _url;
    }

    /**
     * The namespace portion of the LSID.
     * Examples include:
     * <dl>
     *     <dt>Sample</dt>
     *     <dd>The type for a Sample.
     *
     *     <dt>GeneralAssayRun</dt>
     *     <dd>The type for a Standard (formerly called General) assay run</dd>
     *
     *     <dt>AssayRunTSVData</dt>
     *     <dd>The type for the Data output file of an assay run</dd>
     *
     *     <dt>GeneralAssayResultRow</dt>
     *     <dd>The type for an individual assay result row of a Standard assay. (requires provenance module)</dd>
     * </dl>
     */
    public String getType()
    {
        return _type;
    }

    /**
     * The CPAS type of the object as an LSID.
     * For a Sample node, the CPAS type is the LSID of the Sample Type.
     * For a Data in a DataClass, the CPAS type is the LSID of the Data Class.
     * For a run, the CPAS type is the LSID of the run's protocol.
     */
    public String getCpasType()
    {
        return _cpasType;
    }

    /**
     * The Experiment type of the object.
     * <dl>
     *     <dt>Data</dt>
     *     <dd>A file or a data in a DataClass</dd>
     *
     *     <dt>Material</dt>
     *     <dd>A sample in a Sample Type</dd>
     *
     *     <dt>ExperimentRun</dt>
     *     <dd>An experiment run, e.g. a derivation or an assay import</dd>
     * </dl>
     */
    public String getExpType()
    {
        return _expType;
    }

    /**
     * Container entity ID of the object.
     */
    public String getContainer()
    {
        return _container;
    }

    public Date getCreated()
    {
        return _created;
    }

    public String getCreatedBy()
    {
        return _createdBy;
    }

    public Date getModified()
    {
        return _modified;
    }

    public String getModifiedBy()
    {
        return _modifiedBy;
    }

    public String getComment()
    {
        return _comment;
    }

    public String getSchemaName()
    {
        return _schemaName;
    }

    public String getQueryName()
    {
        return _queryName;
    }

    public List<Filter> getPkFilters()
    {
        return _pkFilters;
    }

    public Map<String, Object> getProperties()
    {
        return _properties;
    }

    public List<Edge> getParents()
    {
        return _parents;
    }

    public List<Edge> getChildren()
    {
        return _children;
    }

    void dump(int indent, StringBuilder sb, Set<String> seen)
    {
        indent(indent, sb).append("> ").append(getName()).append(" (").append(getId()).append(")");
        if (seen.contains(getLsid()))
        {
            sb.append(" **\n");
            return;
        }
        sb.append("\n");
        seen.add(getLsid());

        indent(indent+1, sb).append("lsid: ").append(getLsid()).append("\n");
        indent(indent+1, sb).append("type: ").append(getType()).append("\n");
        indent(indent+1, sb).append("exp:  ").append(getExpType()).append("\n");
        indent(indent+1, sb).append("cpas: ").append(getCpasType()).append("\n");
        indent(indent+1, sb).append("url:  ").append(getUrl()).append("\n");

        if (getComment() != null)
            indent(indent+1, sb).append("comment:  ").append(getComment()).append("\n");

        if (getSchemaName() != null)
            indent(indent+1, sb).append("schemaName: ").append(getSchemaName()).append("\n");
        if (getQueryName() != null)
            indent(indent+1, sb).append("queryName: ").append(getQueryName()).append("\n");
        if (getPkFilters() != null && getPkFilters().size() > 0)
            indent(indent + 1, sb).append("pkFilters: ").append(getPkFilters().stream().map(f -> f.getColumnName() + "=" + f.getValue()).collect(Collectors.joining(", ", "[", "]"))).append("\n");

        if (!getProperties().isEmpty())
            indent(indent+1, sb).append("properties: ").append(getProperties()).append("\n");

        indent(indent+1, sb).append("parents:");
        if (_parents.isEmpty())
            sb.append(" (none)");
        sb.append("\n");
        for (Edge edge : _parents)
        {
            indent(indent+2, sb).append("role: ").append(edge.getRole()).append("\n");
            edge.getNode().dump(indent+3, sb, seen);
        }

        indent(indent+1, sb).append("children:");
        if (_children.isEmpty())
            sb.append(" (none)");
        sb.append("\n");
        for (Edge edge : _children)
        {
            indent(indent+2, sb).append("role: ").append(edge.getRole()).append("\n");
            edge.getNode().dump(indent+3, sb, seen);
        }
    }

    StringBuilder indent(int indent, StringBuilder sb)
    {
        for (int i = 0; i < indent; i++)
            sb.append("  ");
        return sb;
    }


    public static class Edge
    {
        private final String role;
        private final LineageNode node;

        Edge(String role, LineageNode node)
        {
            this.role = role;
            this.node = node;
        }

        public String getRole()
        {
            return role;
        }

        public LineageNode getNode()
        {
            return node;
        }
    }
}
