package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;

public class ConditionalFormat
{
    private String queryFilter;
    private String textColor = "";
    private String backgroundColor = "";
    private Boolean bold = false;
    private Boolean italic = false;
    private Boolean strikethrough = false;

    public ConditionalFormat(String queryFilter, String textColor, String backgroundColor, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        this.queryFilter = queryFilter;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.bold = bold;
        this.italic = italic;
        this.strikethrough = strikethrough;
    }

    public ConditionalFormat(String queryFilter, String textColor, String backgroundColor)
    {
        this.queryFilter = queryFilter;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public ConditionalFormat(String queryFilter, Boolean bold, Boolean italic, Boolean strikethrough)
    {
        this.queryFilter = queryFilter;
        this.bold = bold;
        this.italic = italic;
        this.strikethrough = strikethrough;
    }

    public JSONObject toJSON()
    {
        JSONObject conditionalFormat = new JSONObject();
        conditionalFormat.put("filter", queryFilter);
        conditionalFormat.put("backgroundColor", backgroundColor);
        conditionalFormat.put("bold", bold);
        conditionalFormat.put("strikethrough", strikethrough);
        conditionalFormat.put("italic", italic);
        conditionalFormat.put("textColor", textColor);
        return conditionalFormat;
    }

    public String getQueryFilter()
    {
        return queryFilter;
    }

    public void setQueryFilter(String queryFilter)
    {
        this.queryFilter = queryFilter;
    }

    public String getTextColor()
    {
        return textColor;
    }

    public void setTextColor(String textColor)
    {
        this.textColor = textColor;
    }

    public String getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public Boolean getBold()
    {
        return bold;
    }

    public void setBold(Boolean bold)
    {
        this.bold = bold;
    }

    public Boolean getItalic()
    {
        return italic;
    }

    public void setItalic(Boolean italic)
    {
        this.italic = italic;
    }

    public Boolean getStrikethrough()
    {
        return strikethrough;
    }

    public void setStrikethrough(Boolean strikethrough)
    {
        this.strikethrough = strikethrough;
    }
}
