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
package org.labkey.remoteapi.assay;

import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/*
* User: Dave
* Date: Jul 14, 2008
* Time: 1:59:11 PM
*/
public class AssayListCommand extends Command
{
    private String _name;
    private String _type;
    private Integer _id;

    public AssayListCommand()
    {
        super("assay", "assayList");
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getType()
    {
        return _type;
    }

    public void setType(String type)
    {
        _type = type;
    }

    public Integer getId()
    {
        return _id;
    }

    public void setId(Integer id)
    {
        _id = id;
    }

    public AssayListResponse execute(Connection connection, String folderPath) throws IOException, EncoderException
    {
        return (AssayListResponse)super.execute(connection, folderPath);
    }

    protected CommandResponse createResponse(String text, int status)
    {
        return new AssayListResponse(text, status);
    }

    public Map<String, Object> getParameters()
    {
        Map<String,Object> params = new HashMap<String,Object>();
        if(null != getName())
            params.put("name", getName());
        if(null != getType())
            params.put("type", getType());
        if(null != getId())
            params.put("id", getId());

        return params;
    }
}