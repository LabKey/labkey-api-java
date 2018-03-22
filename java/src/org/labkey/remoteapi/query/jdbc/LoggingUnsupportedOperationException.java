package org.labkey.remoteapi.query.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: tgaluhn
 * Date: 3/19/2018
 *
 * Simple extension to UnsupportedOperationException to explicitly log. Useful when a host app for the JDBC driver doesn't bubble up
 * our exception to anyplace useful.
 */
public class LoggingUnsupportedOperationException extends UnsupportedOperationException
{
    private final static Logger _log = Logger.getLogger(LoggingUnsupportedOperationException.class.getName());

    public LoggingUnsupportedOperationException()
    {
        super();
        log("Unsupported operation");
    }

    public LoggingUnsupportedOperationException(String message)
    {
        super(message);
        log(message);
    }

    private void log(String message)
    {
        _log.log(Level.FINE, message, this);
    }
}
