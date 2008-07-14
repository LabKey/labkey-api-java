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

    public Filter(String columnName, Object value)
    {
        _columnName = columnName;
        _value = value;
    }

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

    public String getQueryStringParamName()
    {
        return getColumnName() + "~" + getOperator().getName();
    }

    public String getQueryStringParamValue()
    {
        return getValue().toString();
    }
}