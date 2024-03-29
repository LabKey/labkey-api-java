/*
 * Copyright (c) 2009-2013 LabKey Corporation
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RowsResponseRowset implements Rowset
{
    private final List<Map<String, Object>> _rows;

    public RowsResponseRowset(List<Map<String, Object>> rows)
    {
        _rows = rows;
    }

    @Override
    public int getSize()
    {
        return null != _rows ? _rows.size() : 0;
    }

    @Override
    public Iterator<Row> iterator()
    {
        return new Iterator<>()
        {
            private int _idx = 0;

            @Override
            public boolean hasNext()
            {
                return null != _rows && _idx < _rows.size();
            }

            @Override
            public Row next()
            {
                RowMap row = new RowMap(_rows.get(_idx));
                ++_idx;
                return row;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("You may not remove rows from the rowset.");
            }
        };
    }
}
