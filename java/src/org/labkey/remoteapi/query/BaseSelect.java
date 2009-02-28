package org.labkey.remoteapi.query;

import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.CommandException;

import java.io.IOException;

/**
 * User: adam
 * Date: Feb 27, 2009
 * Time: 11:06:20 AM
 */

// Common methods implemented by SelectRowsCommand and ExecuteSqlCommand.  Makes implementing SAS wrapper classes much easier.
public interface BaseSelect
{
    int getMaxRows();

    void setMaxRows(int maxRows);

    int getOffset();

    void setOffset(int offset);

    ContainerFilter getContainerFilter();

    void setContainerFilter(ContainerFilter containerFilter);

    SelectRowsResponse execute(Connection connection, String folderPath) throws IOException, CommandException;

    double getRequiredVersion();

    void setRequiredVersion(double requiredVersion);
}
