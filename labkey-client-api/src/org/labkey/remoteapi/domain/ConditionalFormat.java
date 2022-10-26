package org.labkey.remoteapi.domain;

import org.json.JSONObject;
import org.labkey.remoteapi.query.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean class for defining a conditional format for a field.<br>
 * Grids will conditionally apply the specified styling to the associated field.
 *
 * @see PropertyDescriptor
 */
public class ConditionalFormat
{
    private List<ConditionalFormatFilter> _queryFilter;
    private String _textColor;
    private String _backgroundColor;
    private boolean _bold;
    private boolean _italic;
    private boolean _strikethrough;

    /**
     * Construct a conditional format with the specified styling. <code>null</code> values for all styles will leave
     * default styling.
     *
     * @param filters define when the format should be applied
     * @param textColor RGB hex value for conditional text color (e.g. "ffffff" for black)
     * @param backgroundColor RGB hex value for conditional background color (e.g. "ffffff" for black)
     * @param bold <code>true</code> to conditionally bold text
     * @param italic <code>true</code> to conditionally italicize text
     * @param strikethrough <code>true</code> to conditionally strike through text
     */
    public ConditionalFormat(List<ConditionalFormatFilter> filters, String textColor, String backgroundColor, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        _queryFilter = filters;
        _textColor = textColor;
        _backgroundColor = backgroundColor;
        _bold = null != bold && bold;
        _italic = null != italic && italic;
        _strikethrough = null != strikethrough && strikethrough;
    }

    /**
     * Construct a conditional format with color styling
     * @param filters define when the format should be applied
     * @param textColor RGB hex value for conditional text color (e.g. "ffffff" for black)
     * @param backgroundColor RGB hex value for conditional background color (e.g. "ffffff" for black)
     */
    public ConditionalFormat(List<ConditionalFormatFilter> filters, String textColor, String backgroundColor)
    {
        this(filters, textColor, backgroundColor, null, null, null);
    }

    /**
     * Construct a conditional format with font styling
     * @param filters define when the format should be applied
     * @param bold <code>true</code> to conditionally bold text
     * @param italic <code>true</code> to conditionally italicize text
     * @param strikethrough <code>true</code> to conditionally strike through text
     */
    public ConditionalFormat(List<ConditionalFormatFilter> filters, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        this(filters, null, null, bold, italic, strikethrough);
    }

    protected String queryFilterToJSONString()
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

    /**
     * Convert bean to JSON for serialization
     * @return JSONObject representation of this conditional format.
     */
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

    protected static List<ConditionalFormatFilter> queryFilterFromJSONString(String filterStr)
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

    /**
     * Populate new ConditionalFormat bean with deserialized JSON
     * @param json deserialized JSON representing a conditional format
     * @return new ConditionalFormat bean
     */
    public static ConditionalFormat fromJSON(JSONObject json)
    {
        String filterStr = (String) json.get("filter");
        return new ConditionalFormat(
            queryFilterFromJSONString(filterStr),
            json.optString("textColor", null),
            json.optString("backgroundColor", null),
            json.optBoolean("bold"),
            json.optBoolean("italic"),
            json.optBoolean("strikethrough")
        );
    }

    /**
     * Get conditional format conditions
     * @return list of filters
     */
    public List<ConditionalFormatFilter> getQueryFilter()
    {
        return _queryFilter;
    }

    /**
     * Set conditional format conditions
     * @param filters list of filters
     */
    public void setQueryFilter(List<ConditionalFormatFilter> filters)
    {
        _queryFilter = filters;
    }

    /**
     * Get conditional text color. Blank or <code>null</code> for no conditional color.
     * @return RGB hex value for text color (e.g. "ffffff" for black)
     */
    public String getTextColor()
    {
        return _textColor;
    }

    /**
     * Set the conditional text color. Blank or <code>null</code> for no conditional color.
     * @param textColor RGB hex value for text color (e.g. "ffffff" for black)
     */
    public void setTextColor(String textColor)
    {
        _textColor = textColor;
    }

    /**
     * Get conditional background color. Blank or <code>null</code> for no conditional color.
     * @return RGB hex value for background color (e.g. "ffffff" for black)
     */
    public String getBackgroundColor()
    {
        return _backgroundColor;
    }

    /**
     * Set the conditional background color. Blank or <code>null</code> for no conditional color.
     * @param backgroundColor RGB hex value for background color (e.g. "ffffff" for black)
     */
    public void setBackgroundColor(String backgroundColor)
    {
        _backgroundColor = backgroundColor;
    }

    /**
     * Get boldness flag for conditional format
     * @return flag
     */
    public boolean getBold()
    {
        return _bold;
    }

    /**
     * Set boldness flag for conditional format
     * @param bold <code>true</code> to conditionally bold field in grids
     */
    public void setBold(boolean bold)
    {
        _bold = bold;
    }

    /**
     * Get italic flag for conditional format
     * @return flag
     */
    public boolean getItalic()
    {
        return _italic;
    }

    /**
     * Set italic flag for conditional format
     * @param italic <code>true</code> to conditionally italicize field in grids
     */
    public void setItalic(boolean italic)
    {
        _italic = italic;
    }

    /**
     * @return strikethrough flag for conditional format
     */
    public boolean getStrikethrough()
    {
        return _strikethrough;
    }

    /**
     * Set strikethrough flag for conditional format
     * @param strikethrough <code>true</code> to conditionally strikethrough field in grids
     */
    public void setStrikethrough(boolean strikethrough)
    {
        _strikethrough = strikethrough;
    }
}
