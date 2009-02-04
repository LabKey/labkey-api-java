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
import org.labkey.remoteapi.query.SaveRowsCommand;
import org.labkey.remoteapi.query.SaveRowsResponse;

import java.io.IOException;

/**
 * User: adam
 * Date: Feb 2, 2009
 * Time: 2:50:35 PM
 */
public abstract class SASSaveRowsCommand
{
    private final SaveRowsCommand _command;

    public SASSaveRowsCommand(SaveRowsCommand command)
    {
        _command = command;
    }

    public void addRow(SASRow row)
    {
        _command.addRow(row.getMap());
    }

    SaveRowsResponse execute(SASConnection cn, String folderPath) throws CommandException, IOException
    {
        return _command.execute(cn, folderPath);
    }
}