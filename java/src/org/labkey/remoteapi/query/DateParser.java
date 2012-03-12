package org.labkey.remoteapi.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: matthewb
 * Date: 2012-03-12
 * Time: 10:55 AM
 *
 * not static because SimpleDatFormat is not thread-safe
 */
public class DateParser
{
    SimpleDateFormat[] formats = new SimpleDateFormat[]
    {
        new SimpleDateFormat("yyyy/MM/d HH:mm:ss"),
        new SimpleDateFormat("yyyy-MM-d HH:mm:ss"),
        new SimpleDateFormat("d MMM yyyy HH:mm:ss")
    };

    public DateParser()
    {
        for (SimpleDateFormat f : formats)
            f.setLenient(true);
    }

    public Date parse(String s) throws ParseException
    {
        ParseException ex = null;
        for (SimpleDateFormat f : formats)
        {
            try
            {
                return f.parse(s);
            }
            catch (ParseException x)
            {
                ex = x;
            }
        }
        if (null != ex)
            throw ex;
        return null;
    }
}
