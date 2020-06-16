package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;
import org.labkey.remoteapi.query.Filter;

import java.util.ArrayList;
import java.util.List;

public class ConditionalFormat
{
    private List<ConditionalFormatFilter> _queryFilter;
    private String _textColor;
    private String _backgroundColor;
    private Boolean _bold;
    private Boolean _italic;
    private Boolean _strikethrough;

    public ConditionalFormat(List<ConditionalFormatFilter> filters, String textColor, String backgroundColor, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        _queryFilter = filters;
        _textColor = (null == textColor) ? "" : textColor;
        _backgroundColor = (null == backgroundColor) ? "" : backgroundColor;
        _bold = (null == bold) ? false : bold;
        _italic = (null == italic) ? false : italic;
        _strikethrough = (null == strikethrough) ? false : strikethrough;
    }

    public ConditionalFormat(List<ConditionalFormatFilter> filters, String textColor, String backgroundColor)
    {
        this(filters, textColor, backgroundColor, null, null, null);
    }

    public ConditionalFormat(List<ConditionalFormatFilter> filters, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        this(filters, null, null, bold, italic, strikethrough);
    }

    public String queryFilterToJSONString()
    {
        String delim = "";
        StringBuilder sb = new StringBuilder();

        for (Filter f : _queryFilter)
        {
            sb.append(delim);
            sb.append(f.getQueryStringParamName()).append("=").append(f.getQueryStringParamValue());
            delim = "&";

        }
        return sb.toString();
    }

    public JSONObject toJSON()
    {
        JSONObject conditionalFormat = new JSONObject();
        conditionalFormat.put("filter", queryFilterToJSONString());
        conditionalFormat.put("backgroundColor", _backgroundColor);
        conditionalFormat.put("bold", _bold);
        conditionalFormat.put("strikethrough", _strikethrough);
        conditionalFormat.put("italic", _italic);
        conditionalFormat.put("textColor", _textColor);
        return conditionalFormat;
    }

    static public List<ConditionalFormatFilter> queryFilterFromJSONString(String filterStr)
    {
        List<ConditionalFormatFilter> queryFilter = new ArrayList<>();

        String[] filterStrs = filterStr.split("&");
        for (String filter : filterStrs)
        {
            String filterExp = filter.substring(filter.indexOf("~") + 1, filter.indexOf("="));
            Filter.Operator op = Filter.Operator.getOperatorFromUrlKey(filterExp);

            String value = filter.substring(filter.lastIndexOf("=") + 1);
            queryFilter.add(new ConditionalFormatFilter(value, op));
        }

        return queryFilter;
    }

    static public ConditionalFormat fromJSON(JSONObject json)
    {
        String filterStr = (String) json.get("filter");
        return new ConditionalFormat(
                queryFilterFromJSONString(filterStr),
                (String) json.get("textColor"),
                (String) json.get("backgroundColor"),
                (Boolean) json.get("bold"),
                (Boolean) json.get("italic"),
                (Boolean) json.get("strikethrough")
        );
    }

    public List<ConditionalFormatFilter> getQueryFilter()
    {
        return _queryFilter;
    }

    public void setQueryFilter(List<ConditionalFormatFilter> filters)
    {
        _queryFilter = filters;
    }

    public String getTextColor()
    {
        return _textColor;
    }

    public void setTextColor(String textColor)
    {
        _textColor = textColor;
    }

    public String getBackgroundColor()
    {
        return _backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor)
    {
        _backgroundColor = backgroundColor;
    }

    public Boolean getBold()
    {
        return _bold;
    }

    public void setBold(Boolean bold)
    {
        _bold = bold;
    }

    public Boolean getItalic()
    {
        return _italic;
    }

    public void setItalic(Boolean italic)
    {
        _italic = italic;
    }

    public Boolean getStrikethrough()
    {
        return _strikethrough;
    }

    public void setStrikethrough(Boolean strikethrough)
    {
        _strikethrough = strikethrough;
    }
}
