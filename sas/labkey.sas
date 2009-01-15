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
 	the connection, executes the command, handles the meta data, sets a default title, and creates a SAS data set
	containing all the data rows,.
*/
%macro sharedQueryHandling();
		/*
			If maxRows or rowOffset params have been specified then set them on the command.
		*/
		%if &maxRows ne %then %do;
			sasCommand.callVoidMethod('setMaxRows', &maxRows);
		%end;

		%if &rowOffset ne %then %do;
			sasCommand.callVoidMethod('setOffset', &rowOffset);
		%end;

		/*
			Create the connection, issue the command, and retrieve the response.
		*/
		declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &lk_url);
		declare javaobj sasResponse ('org/labkey/remoteapi/sas/SASResponse', cn, sasCommand, &folderPath);

		sasResponse.callIntMethod('getColumnCount', columnCount);

		/*
			Initialize the variables that will hold code to execute in the next data step. 
		*/

		length pre $20000;
		length row $20000;
		length post $20000;

		pre = '';
		row = '';
		post = 'isNull = 0;';

		length column $100;
		length type $10;

		/*
			Enumerate the columns and build up macro variables that contain code that will be used
			to create the data set.
		*/
		do index = 0 to columnCount - 1;
			/*
				Skip all hidden columns, unless stripAllHidden = 0
			*/
			isHidden = 0;

			if (&stripAllHidden) then sasResponse.callBooleanMethod('isHidden', index, isHidden);

			if (not isHidden) then do;
				sasResponse.callStringMethod('getColumnName', index, column);
				sasResponse.callStringMethod('getType', strip(column), type);

				/*
					Based on each column type, add the code to prepare, format, and retrieve
					the value of each column
				*/
				if (type = 'STRING') then
					do;
						call cats(pre, 'length ' || column || ' $100;');
						call cats(row, "sasResponse.callStringMethod('getCharacter', '", column, "', ", column, ");");
					end;
				else
					do;
						/*
							If value is null set to missing
						*/
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

		/*
			Store the code variables and the title so they're available in the next data step.
		*/

		call symput('preCode', pre);
		call symput('rowCode', row);
		call symput('postCode', post);
		call symput('title', quote(strip(title)));

		/*
			Stash the response object in the java vm so we can use it in the next data step without
			setting up and reissuing the query.
		*/
		length key $8;
	
		sasResponse.callStringMethod('stash', key);

		/*
			Save the stash key so it's availalbe in the next data step.
		*/
		call symput('key', key);

		/*
			Delete all the java objects created.
		*/
		sasResponse.delete();
		sasCommand.delete();
		cn.delete();
	run;

	/*
		Create the actual data set, using the stashed response and the formatting & data retrieval code
		we created in the data step above.
	*/
	data &dataSetName;
		/*
			Retrieve the stashed response.
		*/
		declare javaobj sasResponse ('org/labkey/remoteapi/sas/SASResponse', "&key");

		/*
			Run the code that initializes all character variables.
		*/
		&preCode;

		sasResponse.callBooleanMethod('getRow', hasAnother);

		/*
			For each row, run the code that retrieves the value of each column and output the row.
		*/
		do while (hasAnother);
			&rowCode;

			output;

			sasResponse.callBooleanMethod('getRow', hasAnother);
		end;

		/*
			Run the code that formats columns such as dates.
		*/
		&postCode;

		/*
			Drop temporary variables.
		*/
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

		/*
			If viewName, filter, colSelect, or colSort params have been specified then set them on the command
		*/
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

		/*
			Set the shared params, issue the query, and create the data set
		*/
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

		/*
			Set the shared params, issue the query, and create the data set
		*/
		%sharedQueryHandling();
%mend executeSql;

/*
	Creates a filter to pass as the %selectRows filter= parameter.  Multiple filters can be specified.
	The IS_MISSING and IS_NOT_MISSING operators don't require a value; all other operators require a value.

	The string generated by this macro consists of calls to the command.addFilter() method.
*/
%macro makeFilter/parmbuff;
	/*
		Eliminate the parentheses
	*/
	%let params = %qsubstr(&syspbuff, 2, %length(&syspbuff) - 2);

	%let code = ;
	%let num = 1;
	%let column = %scan(&params, &num, %str( ,));

	/*
		Loop until no more columns
	*/
	%do %while(&column ne);
		/*
			Get the operator
		*/
		%let num = %eval(&num + 1);
		%let operator = %scan(&params, &num, %str( ,));

		%if %index("EQUALS" "EQUALS_ONE_OF" "NOT_EQUALS" "GREATER_THAN" "GREATER_THAN_OR_EQUAL_TO" "LESS_THAN"
					"LESS_THAN_OR_EQUAL_TO"	"DATE_EQUAL" "DATE_NOT_EQUAL" "NOT_EQUAL_OR_NULL" "CONTAINS"
					"DOES_NOT_CONTAIN" "STARTS_WITH" "DOES_NOT_START_WITH",	&operator) %then
			%do;
				/*
					For operators that require a value, set the column, operator, and value.
				*/
				%let num = %eval(&num + 1);
				%let value = %scan(&params, &num, %str( ,));

				/*
					Get the value and ensure it's surrounded with quotes.  We need to send all values
					(even numerics) as strings, otherwise the server will choke when filtering on integer
					columns with a double value.
				*/
				%let value = "%sysfunc(compress(&value, %str(%'%")))";
				%let code = &code sasCommand.callVoidMethod('addFilter', &column, &operator, &value)%str(;);
			%end;
		%else
			%do;
				%if %index("IS_MISSING" "IS_NOT_MISSING", &operator) %then
					%do;
					/*
						For operators that don't require a value, just set the column and operator.
					*/
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
%mend makeFilter;

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

