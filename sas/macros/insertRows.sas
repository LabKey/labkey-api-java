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

/*
	Retrieves data from the instance of LabKey Server previously specified in %setConnection.
*/
%macro insertRows(baseUrl=&lk_baseUrl, folderPath=&lk_folderPath, schemaName=&lk_schemaName, queryName=&lk_queryName, dsn=);
    proc contents data = &dsn
        out = vars(keep = varnum name type format)
        noprint;
    run;

	data _null_;
		declare javaobj command ('org/labkey/remoteapi/sas/SASInsertRowsCommand', &schemaName, &queryName);

        /*
            TODO: Enumerate the data set and add all rows
        */
		declare javaobj row ('org/labkey/remoteapi/sas/SASRow');

		row.callVoidMethod('clear');
		row.callVoidMethod('add', 'First', 'Pebbles');
		row.callVoidMethod('add', 'Last', 'Flintstone');

        command.callVoidMethod('addRow', row);

		/*
			Create the connection, issue the command, and retrieve the response.
		*/
		declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &baseUrl);
		declare javaobj response ('org/labkey/remoteapi/sas/SASSaveRowsResponse', cn, command, &folderPath);

		response.callIntMethod('getRowsAffected', columnCount);

		put columnCount "rows were added";

		response.delete();
		cn.delete();
        row.delete();
		command.delete();
	run;
%mend insertRows;

