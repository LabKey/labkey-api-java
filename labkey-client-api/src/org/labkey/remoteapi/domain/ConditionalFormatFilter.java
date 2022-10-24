package org.labkey.remoteapi.domain;

import org.labkey.remoteapi.query.Filter;

/**
 * Bean defining a filter used to control when conditional formatting should be applied to a field.
 * @see ConditionalFormat
 */
public class ConditionalFormatFilter extends Filter
{
    /**
     * Construct a filter for conditional formatting.
     * @param value filter value
     * @param op filter operator
     */
    public ConditionalFormatFilter(String value, Operator op)
    {
        super("format.column", value, op);
    }
}
