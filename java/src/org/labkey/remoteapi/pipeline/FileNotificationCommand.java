package org.labkey.remoteapi.pipeline;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.Command;

/**
 * Notifies the server that files may have become available for the pipeline to process. Causes the server to
 * look through all of the jobs that are blocked, waiting for files, and release the ones whose files are now
 * available.
 * User: jeckels
 * Date: Feb 15, 2011
 */
public class FileNotificationCommand extends Command<FileNotificationResponse>
{
    public FileNotificationCommand()
    {
        super("pipeline-analysis", "fileNotification");
    }

    @Override
    protected FileNotificationResponse createResponse(String text, int status, String contentType, JSONObject json)
    {
        return new FileNotificationResponse(text, status, contentType, json, this);
    }
}
