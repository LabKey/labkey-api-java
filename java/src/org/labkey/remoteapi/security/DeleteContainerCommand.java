package org.labkey.remoteapi.security;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.PostCommand;

/**
 * Delete a container (project/folder/workbook) and all of its children. Takes no configuration, just a target container
 * path when executing the command.
 * User: jeckels
 * Date: 9/11/12
 */
public class DeleteContainerCommand extends PostCommand<DeleteContainerResponse>
{
    public DeleteContainerCommand()
    {
        super("core", "deleteContainer");
    }

    public DeleteContainerCommand(DeleteContainerCommand source)
    {
        super(source);
    }

    @Override
    protected DeleteContainerResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new DeleteContainerResponse(text, status, contentType, json, this);
    }

    @Override
    public DeleteContainerCommand copy()
    {
        return new DeleteContainerCommand(this);
    }

}
