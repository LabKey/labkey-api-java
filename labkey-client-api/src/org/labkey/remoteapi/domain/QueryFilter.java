package org.labkey.remoteapi.domain;

public class QueryFilter
{
    public enum Type
    {
        HAS_ANY_VALUE(""),

        EQUAL("eq"),
        DATE_EQUAL("dateeq"),

        NEQ("neq"),
        NOT_EQUAL("neq"),
        DATE_NOT_EQUAL("dateneq"),

        NEQ_OR_NULL("neqornull"),
        NOT_EQUAL_OR_MISSING("neqornull"),

        GT("gt"),
        GREATER_THAN("gt"),
        DATE_GREATER_THAN("dategt"),

        LT("lt"),
        LESS_THAN("lt"),
        DATE_LESS_THAN("datelt"),

        GTE("gte"),
        GREATER_THAN_OR_EQUAL("gte"),
        DATE_GREATER_THAN_OR_EQUAL("dategte"),

        LTE("lte"),
        LESS_THAN_OR_EQUAL("lte"),
        DATE_LESS_THAN_OR_EQUAL("datelte"),

        STARTS_WITH("startswith"),
        DOES_NOT_START_WITH("doesnotstartwith"),

        CONTAINS("contains"),
        DOES_NOT_CONTAIN("doesnotcontain"),

        CONTAINS_ONE_OF("containsoneof"),
        CONTAINS_NONE_OF("containsnoneof"),

        IN("in"),

        EQUALS_ONE_OF("in"),

        NOT_IN("notin"),
        EQUALS_NONE_OF("notin"),

        BETWEEN("between"),
        NOT_BETWEEN("notbetween"),

        IS_BLANK("isblank"),
        IS_NOT_BLANK("isnonblank"),

        MEMBER_OF("memberof");

        public final String label;

        Type(String label) {
            this.label = label;
        }
    }

    final private String queryFilter;

    public QueryFilter(String value, String filterType)
    {
        this.queryFilter = String.format("format.column~%s=%s", filterType, value);
    }

    public QueryFilter(String value, String filterType, String additionalValue, String additionalFilter)
    {
        this.queryFilter = String.format("format.column~%s=%s&format.column~%s=%s",
                filterType, value, additionalFilter, additionalValue);
    }

    public String getQueryFilter()
    {
        return queryFilter;
    }
}
