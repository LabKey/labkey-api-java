/*
 * Copyright (c) 2008-2017 LabKey Corporation
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

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 4:00:03 PM
*/

import java.util.List;

/**
 * Represents a particular sort order entry,
 * which consists of a column name and an optional direction
 */
public class Sort
{
    public enum Direction
    {
        ASCENDING,
        DESCENDING
    }

    private String _columnName;
    private Direction _direction = Direction.ASCENDING;

    public Sort(String columnName)
    {
        _columnName = columnName;
    }

    public Sort(String columnName, Direction direction)
    {
        _columnName = columnName;
        _direction = direction;
    }

    public Sort(Sort source)
    {
        _columnName = source._columnName;
        _direction = source._direction;
    }

    public String getColumnName()
    {
        return _columnName;
    }

    public void setColumnName(String columnName)
    {
        _columnName = columnName;
    }

    public Direction getDirection()
    {
        return _direction;
    }

    public void setDirection(Direction direction)
    {
        _direction = direction;
    }

    /**
     * Constructs the sort query string parameter from the current list of
     * sort definitions. The sort query string parameter is in the form of
     * <i>[-]column,[-]column,...</i> where the optional - is used for
     * a descending sort direction.
     * @param sorts the set of sorts in the query
     * @return The sort query string parameter.
     */
    public static String getSortQueryStringParam(List<Sort> sorts)
    {
        StringBuilder param = new StringBuilder();
        String sep = "";
        for(Sort sort : sorts)
        {
            param.append(sep);
            param.append(sort.toQueryStringParam());
            sep = ",";
        }
        return param.toString();
    }

    /**
     * @return a URL-style representation of this sort parameter. Column names are prefixed with "-" to represent a
     * descending sort.
     */
    public String toQueryStringParam()
    {
        return (getDirection() == Sort.Direction.DESCENDING ? "-" : "") + getColumnName();
    }
}