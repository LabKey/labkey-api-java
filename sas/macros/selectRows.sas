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
%macro selectRows(baseUrl=&lk_baseUrl, folderPath=&lk_folderPath, schemaName=&lk_schemaName, queryName=&lk_queryName, dsn=, viewName=, filter=, colSelect=, colSort=, rowOffset=, maxRows=, showHidden=0);
	data _null_;
		declare javaobj command ('org/labkey/remoteapi/sas/SASSelectRowsCommand', &schemaName, &queryName);

		length title $1000;

		title = cat("Schema: '", &schemaName, "', Query: '", &queryName, "'");

		/*
			If viewName, filter, colSelect, or colSort params have been specified then set them on the command
		*/
		%if &viewName ne %then %do;
			command.callVoidMethod('setViewName', &viewName);
			title = cat(title, ", View: '", &viewName, "'");
		%end;

		%if &filter ne %then %do;
			&filter;
		%end;

		%if &colSelect ne %then %do;
			command.callVoidMethod('setColumns', &colSelect);
		%end;

		%if &colSort ne %then	%do;
			command.callVoidMethod('setSorts', &colSort);
		%end;

		/*
			Set the shared params, issue the query, and create the data set
		*/
		%sharedSelectRowsHandling();
%mend selectRows;
