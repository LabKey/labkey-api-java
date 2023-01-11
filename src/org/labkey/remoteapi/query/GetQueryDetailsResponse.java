/*
 * Copyright (c) 2008-2014 LabKey Corporation
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
package org.labkey.remoteapi.query;

import org.json.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.ResponseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the response from a GetQueriesCommand.
 */
public class GetQueryDetailsResponse extends CommandResponse
{
    public GetQueryDetailsResponse(String text, int statusCode, String contentType, JSONObject json, Command<? extends GetQueryDetailsResponse> sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    /**
     * Returns the schema name the query in this response belongs to.
     * @return The schema name.
     */
    public String getSchemaName()
    {
        return getProperty("schemaName");
    }

    /**
     * Returns the query name that is described by this response
     * @return The query name.
     */
    public String getName()
    {
        return getProperty("name");
    }

    /**
     * Returns the description of the query described by this response
     * @return The query description.
     */
    public String getDescription()
    {
        return getProperty("description");
    }

    /**
     * Returns the title of the query described by this response (often the same as the name, but not always)
     * @return The query title.
     */
    public String getTitle()
    {
        return getProperty("title");
    }

    /**
     * Returns the columns available in the given query name.
     * @return The list of columns available within that query.
     */
    public List<Column> getColumns()
    {
        List<Column> result = new ArrayList<>();
        List<Map<String, Object>> columns = getProperty("columns");
        for (Map<String, Object> column : columns)
        {
            result.add(new Column(column));
        }
        return result;
    }

    public static class Column extends ResponseObject
    {
        private Column(Map<String, Object> map)
        {
            super(map);
        }

        /** @return The name of the column */
        public String getName()
        {
            return (String) getAllProperties().get("name");
        }

        /** @return An optional description of the column */
        public String getDescription()
        {
            return (String) getAllProperties().get("description");
        }

        /** @return The column's data type */
        public String getType()
        {
            return (String) getAllProperties().get("type");
        }

        /** @return The field key for the column. If this column comes from a foreign table, the key is a full path from the source query to this column. */
        public String getFieldKey()
        {
            return (String) getAllProperties().get("fieldKey");
        }

        /** @return true if this column is auto-increment */
        public boolean isAutoIncrement()
        {
            return (Boolean) getAllProperties().get("isAutoIncrement");
        }

        /** @return true if this column should be hidden */
        public boolean isHidden()
        {
            return (Boolean) getAllProperties().get("isHidden");
        }

        /** @return true if this is part of the primary key */
        public boolean isKeyField()
        {
            return (Boolean) getAllProperties().get("isKeyField");
        }

        /** @return true if this column is missing-value enabled */
        public boolean isMvEnabled()
        {
            return (Boolean) getAllProperties().get("isMvEnabled");
        }

        /** @return true if this column can accept nulls */
        public boolean isNullable()
        {
            return (Boolean) getAllProperties().get("isNullable");
        }

        /** @return true if this column is read-only */
        public boolean isReadOnly()
        {
            return (Boolean) getAllProperties().get("isReadOnly");
        }
        
        /** @return true if this column may be edited by the current user */
        public boolean isUserEditable()
        {
            return (Boolean) getAllProperties().get("isUserEditable");
        }

        /** @return true if this column is a version column */
        public boolean isVersionField()
        {
            return (Boolean) getAllProperties().get("isVersionField");
        }

        /** @return true if this column may be selected */
        public boolean isSelectable()
        {
            return (Boolean) getAllProperties().get("isSelectable");
        }
        
        /** @return The user-friendly caption for this column (may differ from name) */
        public String getCaption()
        {
            return (String) getAllProperties().get("caption");
        }

        public Lookup getLookup()
        {
            return getAllProperties().get("lookup") == null ? null : new Lookup((Map<String, Object>)getAllProperties().get("lookup"));
        }

        public Map<String, Object> getProperties()
        {
            return _allProperties;
        }

        public boolean isCalculated()
        {
            return (Boolean) getAllProperties().get("calculated");
        }
    }

    public static class Lookup extends ResponseObject
    {
        public Lookup(Map<String, Object> map)
        {
            super(map);
        }

        /** @return The schema in which the lookup query exists */
        public String getSchemaName()
        {
            return (String)getAllProperties().get("schemaName");
        }

        /** @return The name of the lookup query in that schema */
        public String getQueryName()
        {
            return (String)getAllProperties().get("queryName");
        }

        /** @return The container path if the lookup is defined in a different container */
        public String getContainerPath()
        {
            return (String)getAllProperties().get("containerPath");
        }

        /** @return The column that is normally displayed form the lookup table */
        public String getDisplayColumn()
        {
            return (String)getAllProperties().get("displayColumn");
        }
        
        /** @return The primary key column of the lookup table */
        public String getKeyColumn()
        {
            return (String)getAllProperties().get("keyColumn");
        }



        /** @return true if the lookup table is public (i.e., may be accessed via the API) */
        public boolean isPublic()
        {
            return (Boolean)getAllProperties().get("isPublic");
        }

    }
}
