/*
 * Copyright (c) 2010 LabKey Corporation
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
package org.labkey.remoteapi.assay;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* User: jeckels
* Date: Apr 28, 2010
*/
public class Batch extends ExpObject
{
    private List<Run> _runs = new ArrayList<Run>();

    public Batch()
    {
        super();
    }

    public Batch(JSONObject json)
    {
        super(json);
        if (json.containsKey("runs"))
        {
            JSONArray array = (JSONArray)json.get("runs");
            for (Object o : array)
            {
                JSONObject run = (JSONObject) o;
                _runs.add(new Run(run));
            }
        }
    }

    public List<Run> getRuns()
    {
        return _runs;
    }

    public void setRuns(List<Run> runs)
    {
        _runs = runs;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject result = super.toJSONObject();
        JSONArray runs = new JSONArray();
        for (Run run : _runs)
        {
            runs.add(run.toJSONObject());
        }
        result.put("runs", runs);
        return result;
    }
}
