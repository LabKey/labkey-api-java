package org.labkey.remoteapi.security;

/**
 * Similar to CreateContainerCommand, but supports creating folders from a template
 */
public class CreateFolderCommand extends BaseCreateFolderCommand<CreateFolderCommand>
{
    protected CreateFolderCommand()
    {
        super("createFolder");
    }

    @Override
    CreateFolderCommand getThis()
    {
        return this;
    }
}
