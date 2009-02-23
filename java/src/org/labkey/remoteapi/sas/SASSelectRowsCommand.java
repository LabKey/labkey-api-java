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
import org.labkey.remoteapi.query.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: adam
 * Date: Dec 20, 2008
 * Time: 1:10:52 PM
 */

/*
    The SAS JavaObj interface can't invoke all methods.  For example, parameters can only be double, String, or JavaObj.
    Return types must be void, primitives, or String.  This class is a wrapper around SelectRowsCommand that provides an
    interface that can be used from SAS.
 */
public class SASSelectRowsCommand
{
    private final SelectRowsCommand _command;

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

    public void addFilter(String columnName, String operator)
    {
        addFilter(columnName, operator, null);
    }

    // TODO: Convert date values for DATE_EQUALS / DATE_NOT_EQUALS.  Create addDateFilter?  What about ><, etc.?

    // Values must always come in as Strings.  If we accepted numeric values they'd all arrive as doubles (that's
    // all SAS supports) and the server chokes on int columns filtered on double values.
    public void addFilter(String columnName, String operator, String value)
    {
        Filter.Operator op = Filter.Operator.getOperator(operator);

        if (null == op)
            throw new RuntimeException("Unknown operator");

        if (op.isValueRequired())
        {
            if (null == value)
                throw new RuntimeException("A value is required for operator " + op.getProgrammaticName());
        }
        else
        {
            if (null != value)
                throw new RuntimeException("A value must not be specified for operator " + op.getProgrammaticName());
        }

        _command.addFilter(columnName, value, op);
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
        // "Require" 9.1 first, which is needed for QC, but fall back to 8.3
        _command.setRequiredVersion(9.1);

        try
        {
            return _command.execute(cn, folderPath);
        }
        catch (CommandException e)    // TODO: Create & catch CommandVersionException
        {
            if (!e.getMessage().contains("version of this API"))
                throw e;
        }

        _command.setRequiredVersion(8.3);
        return _command.execute(cn, folderPath);
    }
}
