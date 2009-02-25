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

import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.ApiVersionException;

import java.io.IOException;

/**
 * User: adam
 * Date: Feb 23, 2009
 * Time: 9:37:55 AM
 */
public class SASBaseSelectCommand
{
    // Share the version handling between SASSelectRowsCommand and SASExecuteSqlCommand 
    protected static SelectRowsResponse execute(Command command, SASConnection cn, String folderPath) throws CommandException, IOException
    {
        // "Require" 9.1 first, which is needed for QC, but fall back to 8.3
        command.setRequiredVersion(9.1);

        try
        {
            return (SelectRowsResponse)command.execute(cn, folderPath);
        }
        catch (ApiVersionException ave)
        {
            // Drop through and try again, requiring 8.3
        }

        command.setRequiredVersion(8.3);
        return (SelectRowsResponse)command.execute(cn, folderPath);
    }
}
