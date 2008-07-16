/*
 * Copyright (c) 2008 LabKey Corporation
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
    public enum Operator
    {
        EQUAL("eq", "Equals", true),
        NEQ("neq", "Does Not Equal", true),
        DATE_EQUAL("dateeq", "Equals", true),
        DATE_NOT_EQUAL("dateneq", "Does Not Equal", true),
        NEQ_OR_NULL("neqornull", "Does Not Equal", true),
        ISBLANK("isblank", "Is Blank", false),
        NON_BLANK("isnonblank", "Is Not Blank", false),
        GT("gt", "Is Greater Than", true),
        GTE("gte", "Is Greater Than or Equal To", true),
        LT("lt", "Is Less Than", true),
        LTE("lte", "Is Less Than or Equal To", true),
        CONTAINS("contains", "Contains", true),
        DOES_NOT_CONTAIN("doesnotcontain", "Does Not Contain", true),
        DOES_NOT_START_WITH("doesnotstartwith", "Does Not Start With", true),
        STARTS_WITH("startswith", "Starts With", true);

        private String _name;
        private String _caption;
        private boolean _valueRequired;

        Operator(String name, String caption, boolean valueRequired)
        {
            _name = name;
            _caption = caption;
            _valueRequired = valueRequired;
        }

        public String getName()
        {
            return _name;
        }

        public String getCaption()
        {
            return _caption;
        }

        public boolean isValueRequired()
        {
            return _valueRequired;
        }
    }

    private String _columnName;
    private Operator _operator = Operator.EQUAL;
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
    }

    /**
     * Constructs a filter with a given column name, value and operator.
     * @param columnName The column name to filter.
     * @param value The value to compare it to.
     * @param operator The operator for the comparisson.
     */
    public Filter(String columnName, Object value, Operator operator)
    {
        _columnName = columnName;
        _value = value;
        _operator = operator;
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
                : getColumnName() + "~" + getOperator().getName();
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
}