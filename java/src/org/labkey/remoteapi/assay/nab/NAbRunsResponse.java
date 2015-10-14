/*
 * Copyright (c) 2009-2015 LabKey Corporation
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
package org.labkey.remoteapi.assay.nab;

import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.assay.nab.model.NAbRun;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.List;
/*
 * User: brittp
 * Date: May 15, 2009
 * Time: 11:22:49 AM
 */

/**
 * Response class for the {@link NAbRunsCommand}. This class
 * provides helpful getter method to access particular bits of the parsed
 * response data.  Sample usage:
<pre><code>
 public static void main(String[] args) throws Exception
 {
     // expected parameters:
     // args[0]: username
     // args[1]: password
     Connection conn = new Connection("http://myserver/labkey", args[0], args[1]);
     double avg = getAverageNeutDilution(cn, "/home", "NAB", 50);
     System.out.println("Average dilution where 50 percent neutralization occurs: " + avg);
 }

 public static double getAverageNeutDilution(Connection cn, String folderPath, String assayName, int neutPercent) throws CommandException, IOException
 {
     NAbRunsCommand nabCommand = new NAbRunsCommand();
     nabCommand.setAssayName(assayName);
     nabCommand.setCalculateNeut(true);
     nabCommand.setIncludeFitParameters(false);
     nabCommand.setIncludeStats(false);
     nabCommand.setIncludeWells(false);
     NAbRunsResponse runResponse = nabCommand.execute(cn, folderPath);
     NAbRun[] runs = runResponse.getRuns();
     int totalSamples = 0;
     double totalDilution = 0;
     for (NAbRun run : runs)
     {
         for (NAbSample sample : run.getSamples())
         {
             for (NAbNeutralizationResult neutResult : sample.getNeutralizationResults())
             {
                 // only total non-infinite values (that is, results where we found a neutralizing dilution:
                 if (neutResult.getCutoff() == neutPercent &amp;&amp; !Double.isInfinite(neutResult.getCurveBasedDilution()))
                 {
                     totalSamples++;
                     totalDilution += neutResult.getCurveBasedDilution();
                 }
             }
         }
     }
     return totalDilution/totalSamples;
 }
</code></pre>
 */
public class NAbRunsResponse extends CommandResponse
{
    private NAbRun[] _runs;
    /**
     * Constructs a new CommandResponse, initialized with the provided
     * response text and status code.
     *
     * @param text          The response text
     * @param statusCode    The HTTP status code
     * @param contentType   The response content type
     * @param json          The parsed JSONObject (or null if JSON was not returned).
     * @param sourceCommand A copy of the command that created this response
     */
    public NAbRunsResponse(String text, int statusCode, String contentType, JSONObject json, Command sourceCommand)
    {
        super(text, statusCode, contentType, json, sourceCommand);
    }

    public NAbRun[] getRuns()
    {
        if (_runs == null)
        {
            List<Map<String, Object>> runs = getProperty("runs");
            if (runs == null)
                throw new IllegalStateException("No runs returned from server: server down or malfunctioning?");
            _runs = new NAbRun[runs.size()];
            for (int i = 0; i < runs.size(); i++)
                _runs[i] = new NAbRun(runs.get(i));
        }
        return _runs;
    }

    public String getAssayName()
    {
        return getProperty("assayName");
    }

    public Integer getAssayId()
    {
        return getProperty("assayId");
    }

    public String getAssayDescription()
    {
        return getProperty("assayDescription");
    }
}