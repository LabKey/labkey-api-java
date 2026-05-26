/*
 * Copyright (c) 2022-2026 LabKey Corporation
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
package org.labkey.remoteapi.test;

import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.experiment.LineageCommand;
import org.labkey.remoteapi.experiment.LineageResponse;

class LineageDemo
{
    public static void main(String[] args) throws Exception
    {
        String url = "http://localhost:8080/labkey";
        String folderPath = "/AssayImportProvenance Test";
        String user = "kevink@labkey.com";
        String password = "xxxxxx";

        String lsid = "urn:lsid:labkey.com:GeneralAssayRun.Folder-4780:40166791-3d3f-1039-a854-9b7575483a25";

        LineageCommand cmd = new LineageCommand.Builder(lsid)
                .setParents(false)
                .setChildren(true)
                .setIncludeProperties(true)
                .setIncludeInputsAndOutputs(true)
                .setIncludeRunSteps(true)
                .build();

        Connection conn = new Connection(url, user, password);
        LineageResponse resp = cmd.execute(conn, folderPath);
        System.out.println(resp.dump());
    }
}
