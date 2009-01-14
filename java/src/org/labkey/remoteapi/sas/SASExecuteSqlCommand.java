/*
 * Copyright (c) 2009 LabKey Corporation
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
import org.labkey.remoteapi.query.ExecuteSqlCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;

import java.io.IOException;

/**
 * User: adam
 * Date: Jan 13, 2009
 * Time: 3:52:10 PM
 */
public class SASExecuteSqlCommand
{
    private ExecuteSqlCommand _command;

    public SASExecuteSqlCommand(String schema, String sql)
    {
        _command = new ExecuteSqlCommand(schema, sql);
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
