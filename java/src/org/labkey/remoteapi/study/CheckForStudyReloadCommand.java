/*
 * Copyright (c) 2015-2017 LabKey Corporation
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
package org.labkey.remoteapi.study;

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;

import java.util.Map;

/**
 * Pings an existing LabKey study to check to see if it needs to be reloaded from a study archive on disk.
 * Created by: jeckels
 * Date: 7/12/15
 */
public class CheckForStudyReloadCommand extends Command<CommandResponse>
{
    private final boolean _skipQueryValidation;
    private final boolean _failForUndefinedVisits;

    public CheckForStudyReloadCommand()
    {
        this(true, false);
    }

    public CheckForStudyReloadCommand(boolean skipQueryValidation)
    {
        this(skipQueryValidation, false);
    }

    public CheckForStudyReloadCommand(boolean skipQueryValidation, boolean failForUndefinedVisits)
    {
        super("study", "checkForReload");
        _skipQueryValidation = skipQueryValidation;
        _failForUndefinedVisits = failForUndefinedVisits;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> parameters = super.getParameters();
        if (_skipQueryValidation)
        {
            parameters.put("skipQueryValidation", "1");
        }
        if (_failForUndefinedVisits)
        {
            parameters.put("failForUndefinedVisits", "1");
        }
        return parameters;
    }
}
