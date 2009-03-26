/*
 * Copyright (c) 2009 LabKey Corporation
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
package org.labkey.remoteapi.sas;

import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: adam
 * Date: Jan 10, 2009
 * Time: 11:09:53 PM
 */
public class SASSelectRowsResponse
{
    private final SelectRowsResponse _resp;
    private Iterator<Map<String, Object>> _rowIterator;
    private Map<String, Object> _currentRow;

    private static final Stash<SelectRowsResponse> _stash = new Stash<SelectRowsResponse>(60000);  // Stash entries for up to 60 seconds

    // We need one constructor per command class because of SAS's method-calling limitations (object parameters must match expected class exactly).
    public SASSelectRowsResponse(SASConnection cn, SASSelectRowsCommand command, String folderPath) throws CommandException, IOException
    {
        _resp = command.execute(cn, folderPath);
    }

    public SASSelectRowsResponse(SASConnection cn, SASExecuteSqlCommand command, String folderPath) throws CommandException, IOException
    {
        _resp = command.execute(cn, folderPath);
    }

    public SASSelectRowsResponse(String key)
    {
        _resp = _stash.get(key);
        _rowIterator = _resp.getRows().iterator();
    }

<<<<<<< .working
=======
    private List<Map<String, String>> getFields()
    {
        return (List<Map<String, String>>)_resp.getMetaData().get("fields");
    }

    // Translate all api fields names into legal SAS identifiers.  We'll report these names to SAS and lookup through
    //  these maps when we retrieve data or meta data via name.
    private void createNameMapping(List<Map<String, String>> fields)
    {
        _sasToApiNames = new HashMap<String, String>(fields.size());
        _apiToSasNames = new HashMap<String, String>(fields.size());

        for (Map<String, String> field : fields)
        {
            String apiName = field.get("name");
            String sasName = makeLegal(apiName, _sasToApiNames.keySet());

            _apiToSasNames.put(apiName, sasName);
            _sasToApiNames.put(sasName, apiName);
        }
    }

    private static final Pattern SAS_IDENTIFIER = Pattern.compile("[a-zA-Z_][\\w]{0,31}");
    private static final String LEGAL_FIRST_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private static final String LEGAL_CHARS = LEGAL_FIRST_CHARS + "0123456789";

    public static void testMakeLegal()
    {
        List<String> testNames = new ArrayList<String>(120);
        testNames.addAll(Arrays.asList("Foo", "Foo", "1Foo", "_Foo", "$56TS", "this/that", "howdeedoo_", "howdeedoo#"));

        for (int i = 0; i < 102; i++)
            testNames.add("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi");

        Set<String> identifiers = new HashSet<String>(testNames.size());

        for (String apiName : testNames)
        {
            String sasName = makeLegal(apiName, identifiers);
            if (!isLegal(sasName))
                throw new IllegalStateException(apiName + " translated to " + sasName + ", which is not a legal SAS identifier");

            System.out.println(apiName + " " + sasName);
            identifiers.add(sasName);
        }

        int testCount = 100000;
        identifiers = new HashSet<String>(testCount);
        Random random = new Random();

        for (int i = 0; i < testCount; i++)
        {
            StringBuilder sb = new StringBuilder();
            int length = 1 + random.nextInt(40);

            for (int j = 0; j < length; j++)
                sb.append((char)(32 + random.nextInt(95)));

            String sasName = makeLegal(sb.toString(), identifiers);
            if (!isLegal(sasName))
                throw new IllegalStateException("\"" + sb.toString() + "\" translated to \"" + sasName + "\", which is not a legal SAS identifier");
            if (!identifiers.add(sasName))
                throw new IllegalStateException("\"" + sasName + "\"" + " already exists");
        }
    }

    private static String makeLegal(String apiName, Set<String> currentNames)
    {
        String legalName = apiName;

        if (!isLegal(apiName))
        {
            // Max length is 32 characters
            StringBuilder sasName = new StringBuilder(apiName.substring(0, Math.min(32, apiName.length())));

            // Can't start with a number
            String chars = LEGAL_FIRST_CHARS;

            for (int i = 0; i < sasName.length(); i++)
            {
                // Replace each illegal character with an underscore
                if (-1 == chars.indexOf(sasName.charAt(i)))
                    sasName.setCharAt(i, '_');

                chars = LEGAL_CHARS;
            }

            legalName = sasName.toString();
        }

        // It's legal, but we may have a clash with an earlier identifier

        int i = 1;
        String candidateName = legalName;

        // Append (or replace last chars if 32 chars) with 2, 3, 4, 5, etc. until we have a unique identifier
        while (currentNames.contains(candidateName))
        {
            i++;
            String suffix = String.valueOf(i);

            if (legalName.length() + suffix.length() > 32)
                candidateName = legalName.substring(0, 32 - suffix.length()) + suffix;
            else
                candidateName = legalName + suffix;
        }

        return candidateName;
    }

    private static boolean isLegal(String identifier)
    {
        return identifier.length() <= 32 && SAS_IDENTIFIER.matcher(identifier).matches();
    }

    private String getSasName(String apiName)
    {
        return _apiToSasNames.get(apiName);
    }

    private String getApiName(String apiName)
    {
        return _sasToApiNames.get(apiName.trim());
    }

>>>>>>> .merge-right.r10720
    public int getColumnCount()
    {
        List<Map> fields = (List<Map>)_resp.getMetaData().get("fields");
        return fields.size();
    }

    public String getColumnName(double index)
    {
        int i = (int)Math.round(index);
        List<Map<String, String>> fields = (List<Map<String, String>>)_resp.getMetaData().get("fields");
        return fields.get(i).get("name");
    }

    public String getType(String columnName)
    {
        SelectRowsResponse.ColumnDataType type = _resp.getColumnDataType(columnName);
        return type.toString();
    }

    private Object getColumnModelProperty(double index, String propName)
    {
        int i = (int)Math.round(index);
        Map<String, Object> columnModel = _resp.getColumnModel().get(i);
        return columnModel.get(propName);
    }

    public boolean isHidden(double index)
    {
        return (Boolean)getColumnModelProperty(index, "hidden");
    }

    public int getScale(double index)
    {
        int scale = ((Long)getColumnModelProperty(index, "scale")).intValue();
        return 0 == scale ? 100 : scale;  // TODO: Temp hack -- 8.3 servers return different scale values than 9.1 -- for strings, scale == 0 at times
    }

    public String getLabel(double index)
    {
        return (String)getColumnModelProperty(index, "header");
    }

    public boolean allowsMissingValues(String sasName)
    {
        Boolean allowsQC = (Boolean)_resp.getMetaData(columnName).get("allowsQC");

        return (null != allowsQC && allowsQC);
    }

    public String getMissingValuesCode()
    {
        Map<String, String> qcInfo = (Map<String, String>)_resp.getParsedData().get("qcInfo");

        StringBuilder values = new StringBuilder();
        StringBuilder footnotes = new StringBuilder();

        if (null != qcInfo)
        {
            int count = 1;

            for (Map.Entry<String, String> missing : qcInfo.entrySet())
            {
                values.append(" ").append(missing.getKey());

                if (count <= 10)
                    footnotes.append("footnote").append(count++).append(" \"").append(missing.getKey()).append(" - ").append(missing.getValue()).append("\";\n");
            }
        }

        if (values.length() > 0)
            return "missing" + values + ";\n" + footnotes;
        else
            return "footnote;";
    }

    public String stash()
    {
        return _stash.put(_resp);
    }

    public boolean getRow()
    {
        boolean hasNext = _rowIterator.hasNext();

        if (hasNext)
            _currentRow = _rowIterator.next();

        return hasNext;
    }


    /*  TODO: Move the following methods to SASRow and return (new up) a SASRow instead of using getRow()? */

    // Detect and handle both LabKey 8.3 and 9.1 formats
    private Object getValue(String key)
    {
        Object o = _currentRow.get(key);

        if (_resp.getRequiredVersion() < 9.1)
            return o;
        else
            return ((JSONObject)o).get("value");
    }

    public boolean isNull(String key)
    {
        return null == getValue(key);
    }

    public String getCharacter(String key)
    {
        return (String)getValue(key);
    }

    public double getNumeric(String key)
    {
        return ((Number)getValue(key)).doubleValue();
    }

    public boolean getBoolean(String key)
    {
        return (Boolean)getValue(key);
    }

    private DateFormat _df = new SimpleDateFormat("yyyy-MM-dd");

    public String getDate(String sasName)
    {
        Date d = (Date)getValue(sasName);
        return _df.format(d);
    }

    public String getMissingValue(String key)
    {
        if (_resp.getRequiredVersion() < 9.1)
            throw new IllegalStateException("Missing values are only available when requiring LabKey 9.1 or later version");

        Object o = _currentRow.get(key);

        return (String)((JSONObject)o).get("qcValue");
    }
}
