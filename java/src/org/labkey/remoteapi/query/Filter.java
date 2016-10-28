/*
 * Copyright (c) 2008-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.remoteapi.query;

import java.util.Map;
import java.util.HashMap;

/*
* User: Dave
* Date: Jul 10, 2008
* Time: 4:17:57 PM
*/

/**
 * Represents a filter definition for a query
 */
public class Filter
{
    @SuppressWarnings({"UnusedDeclaration"})
    public enum Operator
    {
        // WARNING: Keep in sync and in order with all other client apis and docs:
        // - server: CompareType.java
        // - java: Filter.java
        // - js: Filter.js
        // - R: makeFilter.R, makeFilter.Rd
        // - SAS: labkeymakefilter.sas, labkey.org SAS docs
        // - Python & Perl don't have an filter operator enum

        //
        // These operators require a data value
        //

        EQUAL("Equals", "eq", "EQUAL", true),
        DATE_EQUAL("Equals", "dateeq", "DATE_EQUAL", true),

        NEQ("Does Not Equal", "neq", "NOT_EQUAL", true),
        DATE_NOT_EQUAL("Does Not Equal", "dateneq", "DATE_NOT_EQUAL", true),

        NEQ_OR_NULL("Does Not Equal", "neqornull", "NOT_EQUAL_OR_MISSING", true),

        GT("Is Greater Than", "gt", "GREATER_THAN", true),
        DATE_GT("(Date) Is Greater Than", "dategt", "DATE_GREATER_THAN", true),

        LT("Is Less Than", "lt", "LESS_THAN", true),
        DATE_LT("(Date) Is Less Than", "datelt", "DATE_LESS_THAN", true),

        GTE("Is Greater Than or Equal To", "gte", "GREATER_THAN_OR_EQUAL", true),
        DATE_GTE("(Date) Is Greater Than or Equal To", "dategte", "DATE_GREATER_THAN_OR_EQUAL", true),

        LTE("Is Less Than or Equal To", "lte", "LESS_THAN_OR_EQUAL", true),
        DATE_LTE("(Date) Is Less Than or Equal To", "datelte", "DATE_LESS_THAN_OR_EQUAL", true),

        STARTS_WITH("Starts With", "startswith", "STARTS_WITH", true),
        DOES_NOT_START_WITH("Does Not Start With", "doesnotstartwith", "DOES_NOT_START_WITH", true),

        CONTAINS("Contains", "contains", "CONTAINS", true),
        DOES_NOT_CONTAIN("Does Not Contain", "doesnotcontain", "DOES_NOT_CONTAIN", true),

        CONTAINS_ONE_OF("Contains One Of (example usage: a;b;c)", "containsoneof", "CONTAINS_ONE_OF", true),
        CONTAINS_NONE_OF("Does Not Contain Any Of (example usage: a;b;c)", "containsnoneof", "CONTAINS_NONE_OF", true),

        IN("Equals One Of", "in", "IN", true),
        NOT_IN("Does Not Equal Any Of (example usage: a;b;c)", "notin", "NOT_IN", true),

        BETWEEN("Between, Inclusive (example usage: -4,4)", "between", "BETWEEN", true),
        NOT_BETWEEN("Not Between, Exclusive (example usage: -4,4)", "notbetween", "NOT_BETWEEN", true),

        MEMBER_OF("Member Of", "memberof", "MEMBER_OF", true),

        //
        // These are the "no data value" operators
        //

        ISBLANK("Is Blank", "isblank", "MISSING", false),
        @Deprecated // Use NONBLANK instead... that name matches CompareType operator
        NON_BLANK("Is Not Blank", "isnonblank", "NOT_MISSING", false),
        NONBLANK("Is Not Blank", "isnonblank", "NOT_MISSING", false),

        MV_INDICATOR("Has Missing Value Indicator", "hasmvvalue", "MV_INDICATOR", false),
        NO_MV_INDICATOR("Does Not Have Missing Value Indicator", "nomvvalue", "NO_MV_INDICATOR", false),

        //
        // Table/Query-wise operators
        //

        Q("Search", "q", "Q", true),
        WHERE("Where", "where", "WHERE", true)
        ;

        private static final Map<String, Operator> _programmaticNameToOperator = new HashMap<>(Operator.values().length);

        static
        {
            for (Operator o : Operator.values())
                _programmaticNameToOperator.put(o.getProgrammaticName(), o);
        }

        private final String _urlKey;
        private final String _displayValue;
        private final String _programmaticName;
        private final boolean _dataValueRequired;

        // Note: These parameters should match the first four parameters of CompareType() exactly
        Operator(String displayValue, String urlKey, String programmaticName, boolean dataValueRequired)
        {
            _displayValue = displayValue;
            _urlKey = urlKey;
            _programmaticName = programmaticName;
            _dataValueRequired = dataValueRequired;
        }

        public String getDisplayValue()
        {
            return _displayValue;
        }

        public String getUrlKey()
        {
            return _urlKey;
        }

        public boolean isDataValueRequired()
        {
            return _dataValueRequired;
        }

        public String getProgrammaticName()
        {
            return _programmaticName;
        }

        public static Operator getOperator(String programmaticName)
        {
            return _programmaticNameToOperator.get(programmaticName);
        }

        @Deprecated // Use getDisplayValue()... this method is for backward compatibility
        public String getCaption()
        {
            return _displayValue;
        }

        @Deprecated // Use getUrlKey()... this method is for backward compatibility
        public String getName()
        {
            return _urlKey;
        }

        @Deprecated // Use isDataValueRequired()... this method is for backward compatibility
        public boolean isValueRequired()
        {
            return _dataValueRequired;
        }
    }

    private String _columnName;
    private Operator _operator;
    private Object _value;

    /**
     * Constructs a new equality Filter for the given column name
     * and value. By default, the operator will be set to {@link Filter.Operator#EQUAL}.
     * @param columnName The column name.
     * @param value The value it should be equal to.
     */
    public Filter(String columnName, Object value)
    {
        _columnName = columnName;
        _value = value;
        _operator = Operator.EQUAL;
    }

    /**
     * Constructs a filter with a given column name, value and operator.
     * @param columnName The column name to filter.
     * @param value The value to compare it to.
     * @param operator The operator for the comparison.
     */
    public Filter(String columnName, Object value, Operator operator)
    {
        _columnName = columnName;
        _value = value;
        _operator = operator;
    }

    public Filter(Filter source)
    {
        _columnName = source._columnName;
        _operator = source._operator;
        _value = source._value; //might be a shallow copy
    }

    public String getColumnName()
    {
        return _columnName;
    }

    public void setColumnName(String columnName)
    {
        _columnName = columnName;
    }

    public Operator getOperator()
    {
        return _operator;
    }

    public void setOperator(Operator operator)
    {
        _operator = operator;
    }

    public Object getValue()
    {
        return _value;
    }

    public void setValue(Object value)
    {
        _value = value;
    }

    /**
     * Returns the query string parameter name for this filter.
     * Because query string parameters contain only two items
     * (name = value), we encode the operator in the name,
     * and this method returns the appropriate encoding.
     * @return The query string parameter name.
     */
    public String getQueryStringParamName()
    {
        return (null == getColumnName() || null == getOperator())
                ? ""
                : getColumnName() + "~" + getOperator().getUrlKey();
    }

    /**
     * Returns the query string parameter value (not URL-encoded). By default,
     * this simply returns the results of the <code>value.toString()</code>.
     * Extended classes may override this to do a different string encoding.
     * Note that this value will be URL-encoded by the caller, so do
     * not URL-encode the value returned from this method.
     * @return The query string parameter value.
     */
    public String getQueryStringParamValue()
    {
        return null == getValue() ? "" : getValue().toString();
    }

    @Override
    public String toString()
    {
        return "Filter: " + getColumnName() + " " + getOperator().getDisplayValue() + " " + getValue();
    }
}
