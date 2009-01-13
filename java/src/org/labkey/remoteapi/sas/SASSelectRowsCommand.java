/*
 * Copyright (c) 2008 LabKey Corporation
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
package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.remoteapi.query.Sort;

import java.io.IOException;
import java.util.*;

/**
 * User: adam
 * Date: Dec 20, 2008
 * Time: 1:10:52 PM
 */

/*
    The SAS JavaObj interface can't invoke all methods.  For example, return types must be void, double, or String.
    This class is a wrapper around SelectRowsCommand that provides an interface that's usable from SAS.
 */
public class SASSelectRowsCommand
{
    private SelectRowsCommand _command;

    public SASSelectRowsCommand(String schema, String query)
    {
        _command = new SelectRowsCommand(schema, query);
    }

    public void setViewName(String viewName)
    {
        _command.setViewName(viewName);
    }

    public void setColumns(String columns)
    {
        String[] array = columns.split(",");
        List<String> list = new ArrayList<String>(array.length);

        for (String name : array)
            list.add(name.trim());

        _command.setColumns(list);
    }

    public void setSorts(String columns)
    {
        String[] array = columns.split(",");
        List<Sort> list = new ArrayList<Sort>(array.length);

        for (String name : array)
        {
            String trimmed = name.trim();
            Sort sort = trimmed.startsWith("-") ? new Sort(trimmed.substring(1), Sort.Direction.DESCENDING) : new Sort(trimmed);
            list.add(sort);
        }

        _command.setSorts(list);
    }

    public void setMaxRows(double maxRows)
    {
        int max = (int)Math.round(maxRows);
        _command.setMaxRows(max);
    }

    public void setOffset(double offset)
    {
        int off = (int)Math.round(offset);
        _command.setOffset(off);
    }

    SelectRowsResponse execute(SASConnection cn, String folderPath) throws CommandException, IOException
    {
        return _command.execute(cn, folderPath);
    }
}
