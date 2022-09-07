package org.labkey.remoteapi.security;

import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

/**
 * Similar to CreateContainerCommand, but supports the "Project Creator" role and creating projects from a template
 */
public class CreateProjectCommand extends BaseCreateFolderCommand<CreateProjectCommand>
{
    private boolean _assignProjectAdmin = false;

    public CreateProjectCommand()
    {
        super("createProject");
    }

    @Override
    CreateProjectCommand getThis()
    {
        return this;
    }

    public CreateProjectCommand setAssignProjectAdmin(boolean assignProjectAdmin)
    {
        _assignProjectAdmin = assignProjectAdmin;
        return getThis();
    }

    @Override
    protected List<BasicNameValuePair> getPostData()
    {
        List<BasicNameValuePair> postData = super.getPostData();
        postData.add(new BasicNameValuePair("assignProjectAdmin", Boolean.toString(_assignProjectAdmin)));

        return postData;
    }
}
