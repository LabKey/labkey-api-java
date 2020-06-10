package org.labkey.remoteapi.domain;

import org.json.simple.JSONObject;

public class ConditionalFormat
{
    String queryFilter;
    String textColor = "";
    String backgroundColor = "";
    Boolean bold = false;
    Boolean italic = false;
    Boolean strikethrough = false;

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
}
