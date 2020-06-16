package org.labkey.remoteapi.domain;

import org.labkey.remoteapi.query.Filter;

public class ConditionalFormatFilter extends Filter
{
    public ConditionalFormatFilter(String value, Operator op)
    {
        super("format.column", value, op);
    }
}
