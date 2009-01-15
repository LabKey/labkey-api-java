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
	Common code used by %executeSql and %selectRows.  Handles maxRows & rowOffset params (if present), initializes
 	the connection, executes the command, handles the meta data, creates a SAS data set containing all the data rows,
	and sets a default title.
*/
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
%mend sharedQueryHandling;

/*
	Stores the base URL to an instance of LabKey Server in a global macro variable that's used by all other macros.
*/
%macro setConnection(baseUrl);
	%global lk_url;
	%let lk_url = &baseUrl;
%mend setConnection;

/*
	Retrieves data from the instance of LabKey Server previously specified in %setConnection.
*/
%macro selectRows(folderPath, schemaName, queryName, dataSetName, viewName=, filter=, colSelect=, colSort=, rowOffset=, maxRows=, stripAllHidden=1);
	data _null_;
		declare javaobj sasCommand ('org/labkey/remoteapi/sas/SASSelectRowsCommand', &schemaName, &queryName);

		length title $1000;

		title = "Schema: &schemaName, Query: &queryName";

		%if &viewName ne %then %do;
			sasCommand.callVoidMethod('setViewName', &viewName);
			call cats(title, ", View: &viewName");
		%end;

		%if &filter ne %then %do;
			&filter;
		%end;

		%if &colSelect ne %then %do;
			sasCommand.callVoidMethod('setColumns', &colSelect);
		%end;

		%if &colSort ne %then	%do;
			sasCommand.callVoidMethod('setSorts', &colSort);
		%end;

		%sharedQueryHandling();
%mend selectRows;

/*
	Executes a SQL query against the instance of LabKey Server previously specified in %setConnection.
*/
%macro executeSql(folderPath, schemaName, sql, dataSetName, rowOffset=, maxRows=, stripAllHidden=1);
	data _null_;
		declare javaobj sasCommand ('org/labkey/remoteapi/sas/SASExecuteSqlCommand', &schemaName, &sql);

		length title $1000;

		title = "Schema: &schemaName, SQL: &sql";

		%sharedQueryHandling();
%mend executeSql;

%macro makeFilter/parmbuff;
	%let params = %qsubstr(&syspbuff, 2, %length(&syspbuff) - 2);
	%put params: &params;

	%let num = 1;
	%let column = %scan(&params, &num, %str( ,));
	%let code = ;
	%do %while(&column ne);
		%let num = %eval(&num + 1);
		%let operator = %scan(&params, &num, %str( ,));

		%if %index("EQUALS" "EQUALS_ONE_OF" "NOT_EQUALS" "GREATER_THAN" "GREATER_THAN_OR_EQUAL_TO" "LESS_THAN" "LESS_THAN_OR_EQUAL_TO"
					"DATE_EQUAL" "DATE_NOT_EQUAL" "NOT_EQUAL_OR_NULL" "CONTAINS" "DOES_NOT_CONTAIN" "STARTS_WITH" "DOES_NOT_START_WITH",
					&operator) %then
			%do;
				%let num = %eval(&num + 1);
				%let value = %scan(&params, &num, %str( ,));
				%let value = "%sysfunc(compress(&value, %str(%'%")))";
				%let code = &code sasCommand.callVoidMethod('addFilter', &column, &operator, &value)%str(;);
			%end;
		%else
			%do;
				%if %index("IS_MISSING" "IS_NOT_MISSING", &operator) %then
					%do;
						%let code = &code sasCommand.callVoidMethod('addFilter', &column, &operator)%str(;);
					%end;
				%else
					%do;
						%let code = &code ERROR "Bad operator: &operator"%str(;);
					%end;
			%end;
		
   		%let num = %eval(&num + 1);
		%let column = %scan(&params, &num, %str( ,));
	%end;
	%quote(&code);
	%put %quote(&code);
%mend makeFilter;

%macro test();
	data _null_;
		declare javaobj sasCommand ('org/labkey/remoteapi/sas/SASSelectRowsCommand', "lists", "People");

		sasCommand.callVoidMethod('addFilter', "Age", "GREATER_THAN_OR_EQUAL_TO", "11");

		sasCommand.delete();
	run;
%mend test;

options mprint;

%setConnection('http://localhost:8080/labkey');

%selectRows('/home', 'Lists', 'People', all);

proc print;
run;

%selectRows('/home', 'Lists', 'People', filtered, filter=%makeFilter("Age", "IS_NOT_MISSING", "Height", "LESS_THAN_OR_EQUAL_TO", 1.7));

proc print;
run;

%selectRows('/home', 'Lists', 'People', subset, viewName='namesByAge', colSelect='First, Last', colSort='Last, -First', maxRows=3, rowOffset=1);

proc print;
run;

%executeSql('/home', 'Lists', 'SELECT People.Last, People.First FROM People', sql);

proc print;
run;

%setConnection('https://atlas.scharp.org/cpas');
%selectRows('/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project', 'study', 'Monogram NAb', nab, colSelect='ConcentrationValue, PercentInhibition');

proc print;
run;

