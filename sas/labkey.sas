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
	Stores the base URL to an instance of LabKey Server in a global macro variable that's used by all other macros.
*/
%macro setConnection(baseUrl);
	%global lk_url;
	%let lk_url = &baseUrl;
%mend;

/*
	Retrieves data from the instance of LabKey Server previously specified in %setConnection.
*/
%macro selectRows(folderPath, schemaName, queryName, dataSetName, viewName=, colSelect=, colSort=, rowOffset=, maxRows=, stripAllHidden=1);
	data _null_;
		declare javaobj sasCommand ('org/labkey/remoteapi/sas/SASSelectRowsCommand', &schemaName, &queryName);

		length title $1000;

		title = "Schema: &schemaName, Query: &queryName";

		%if &viewName ne %then %do;
			sasCommand.callVoidMethod('setViewName', &viewName);
			call cats(title, ", View: &viewName");
		%end;

		%if &colSelect ne %then %do;
			sasCommand.callVoidMethod('setColumns', &colSelect);
		%end;

		%if &colSort ne %then	%do;
			sasCommand.callVoidMethod('setSorts', &colSort);
		%end;

		%sharedQueryHandling();
%mend;

%macro sharedQueryHandling();
		%if &maxRows ne %then %do;
			sasCommand.callVoidMethod('setMaxRows', &maxRows);
		%end;

		%if &rowOffset ne %then %do;
			sasCommand.callVoidMethod('setOffset', &rowOffset);
		%end;

		declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &lk_url);
		declare javaobj sasResponse ('org/labkey/remoteapi/sas/SASResponse', cn, sasCommand, &folderPath);

		sasResponse.callIntMethod('getColumnCount', columnCount);

		length pre $20000;
		length row $20000;
		length post $20000;

		pre = '';
		row = '';
		post = 'isNull = 0;';

		length column $100;
		length type $10;

		do index = 0 to columnCount - 1;
			isHidden = 0;

			if (&stripAllHidden) then sasResponse.callBooleanMethod('isHidden', index, isHidden);

			if (not isHidden) then do;
				sasResponse.callStringMethod('getColumnName', index, column);
				sasResponse.callStringMethod('getType', strip(column), type);

				if (type = 'STRING') then
					do;
						call cats(pre, 'length ' || column || ' $100;');
						call cats(row, "sasResponse.callStringMethod('getCharacter', '", column, "', ", column, ");");
					end;
				else
					do;
						call cats(row, "call missing(", column, ");sasResponse.callBooleanMethod('isNull', '", column, "', isNull);if not isNull then sasResponse.callDoubleMethod('");

						if (type = 'DATE') then
							do;
								call cats(row, "getDate");
								call cats(post, "format " || column || " DATE9.;");
							end;
						else
							do;
								call cats(row, "getNumeric");
							end;

						call cats(row, "', '", column, "', ", column, ");");
					end;
				output;
			end;
		end;

		call symput('preCode', pre);
		call symput('rowCode', row);
		call symput('postCode', post);
		call symput('title', quote(strip(title)));

		length key $8;
	
		sasResponse.callStringMethod('stash', key);

		call symput('key', key);

		sasResponse.delete();
		sasCommand.delete();
		cn.delete();
	run;

	data &dataSetName;
		declare javaobj sasResponse ('org/labkey/remoteapi/sas/SASResponse', "&key");

		sasResponse.callBooleanMethod('getRow', hasAnother);

		&preCode;

		do while (hasAnother);
			&rowCode;

			output;

			sasResponse.callBooleanMethod('getRow', hasAnother);
		end;

		&postCode;

		drop hasAnother isNull;

		title &title;

		sasResponse.delete();
	run
%mend;

/*
	Executes a SQL query against the instance of LabKey Server previously specified in %setConnection.
*/
%macro executeSql(folderPath, schemaName, sql, dataSetName, rowOffset=, maxRows=, stripAllHidden=1);
	data _null_;
		declare javaobj sasCommand ('org/labkey/remoteapi/sas/SASExecuteSqlCommand', &schemaName, &sql);

		length title $1000;

		title = "Schema: &schemaName, SQL: &sql";

		%sharedQueryHandling();
%mend;

options mprint;

%setConnection('http://localhost:8080/labkey');
%executeSql('/home', 'Lists', 'SELECT People.Last, People.First FROM People', sql);

/*
%selectRows('/home', 'Lists', 'People', all);
%selectRows('/home', 'Lists', 'People', subset, viewName='namesByAge', colSelect='First, Last', colSort='Last, -First', maxRows=3, rowOffset=1);

%setConnection('https://atlas.scharp.org/cpas');
%selectRows('/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project', 'study', 'Monogram NAb', nab, colSelect='ConcentrationValue, PercentInhibition');
*/
proc print;
run;

