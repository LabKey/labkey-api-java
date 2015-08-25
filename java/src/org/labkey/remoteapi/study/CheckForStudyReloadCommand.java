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

    public CheckForStudyReloadCommand()
    {
        this(true);
    }

    public CheckForStudyReloadCommand(boolean skipQueryValidation)
    {
        super("study", "checkForReload");
        _skipQueryValidation = skipQueryValidation;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> parameters = super.getParameters();
        if (_skipQueryValidation)
        {
            parameters.put("skipQueryValidation", "1");
        }
        return parameters;
    }
}
