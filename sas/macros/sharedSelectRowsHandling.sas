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
	containing all the data rows.
*/
%macro sharedSelectRowsHandling();
		/*
			If maxRows or rowOffset params have been specified then set them on the command.
		*/
		%if &maxRows ne %then %do;
			command.callVoidMethod('setMaxRows', &maxRows);
		%end;

		%if &rowOffset ne %then %do;
			command.callVoidMethod('setOffset', &rowOffset);
		%end;

		/*
			Create the connection, issue the command, and retrieve the response.
		*/
		declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &baseUrl);
		declare javaobj response ('org/labkey/remoteapi/sas/SASSelectRowsResponse', cn, command, &folderPath);

		response.callIntMethod('getColumnCount', columnCount);

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
				Skip all hidden columns, unless showHidden = 1
			*/
			isHidden = 0;

			if (not &showHidden) then response.callBooleanMethod('isHidden', index, isHidden);

			if (not isHidden) then do;
				response.callStringMethod('getColumnName', index, column);
				response.callStringMethod('getType', strip(column), type);

				/*
					Based on each column type, add the code to prepare, format, and retrieve
					the value of each column
				*/
				if (type = 'STRING') then
					do;
						call cats(pre, 'length ' || column || ' $100;');
						call cats(row, "response.callStringMethod('getCharacter', '", column, "', ", column, ");");
					end;
				else
					do;
						/*
							If value is null set to missing
						*/
						call cats(row, "call missing(", column, ");response.callBooleanMethod('isNull', '", column, "', isNull);if not isNull then response.callDoubleMethod('");

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
	
		response.callStringMethod('stash', key);

		/*
			Save the stash key so it's availalbe in the next data step.
		*/
		call symput('key', key);

		/*
			Delete all the java objects created.
		*/
		response.delete();
		command.delete();
		cn.delete();
	run;

	/*
		Create the actual data set, using the stashed response and the formatting & data retrieval code
		we created in the data step above.
	*/
	data &dsn;
		/*
			Retrieve the stashed response.
		*/
		declare javaobj response ('org/labkey/remoteapi/sas/SASSelectRowsResponse', "&key");

		/*
			Run the code that initializes all character variables.
		*/
		&preCode;

		response.callBooleanMethod('getRow', hasAnother);

		/*
			For each row, run the code that retrieves the value of each column and output the row.
		*/
		do while (hasAnother);
			&rowCode;

			output;

			response.callBooleanMethod('getRow', hasAnother);
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

		response.delete();
	run
%mend sharedSelectRowsHandling;
