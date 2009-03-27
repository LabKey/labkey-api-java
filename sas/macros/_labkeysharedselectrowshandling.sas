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
	Common code used by %labkeyExecuteSql and %labkeySelectRows.  Handles maxRows, rowOffset, and containerFilter
	params (if present), initializes the connection, executes the command, handles the meta data, sets a default title,
	and creates a SAS data set containing all the data rows.
*/
%macro _labkeySharedSelectRowsHandling(selectRows);
        %_labkeySendCommand(&selectRows, 1);

		response.callIntMethod('getColumnCount', columnCount);

		/*
			Initialize the variables that will hold code to execute in the next data step. 
		*/

		length pre $20000;
		length row $20000;
		length post $20000;

		row = '';
		post = '';

		length column $100;
		length labelText $100;
		length type $10;
	    length scale 4;

	    response.callStringMethod('getMissingValuesCode', pre);
		call cats(pre, 'isNull = 0;length missingValue $2;missingValue = "";');

		/*
			Enumerate the columns and build up macro variables that contain code that will be used
			to create the data set.
		*/
		do index = 0 to columnCount - 1;
			/*
				Skip all hidden columns, unless showHidden = 1
			*/
			isHidden = 0;
			allowsMissing = 0;
			hasDate = 0;

			%if (not &showHidden) %then %do;
			    response.callBooleanMethod('isHidden', index, isHidden);
			%end;

			if (not isHidden) then do;
				response.callStringMethod('getColumnName', index, column);
				response.callStringMethod('getType', strip(column), type);

				/*
					Based on each column type, add the code to prepare, format, and retrieve
					the value of each column
				*/
				if (type = 'STRING') then
					do;
        				response.callIntMethod('getScale', index, scale);
						call cats(pre, 'length ' || trim(column) || ' $', strip(put(scale, 6.)), ';');
						call cats(row, "response.callStringMethod('getCharacter', '", column, "', ", column, ");");
					end;
				else
					do;
						/*
							If value is null then set to missing
						*/
						call cats(row, "call missing(", column, ");response.callBooleanMethod('isNull', '", column, "', isNull);if not isNull the");

						if (type = 'DATE') then
							do;
        						call cats(pre, 'length ' || strip(column) || ' 8;');

        						if (not hasDate) then do;
        						    call cats(pre, 'length ____date $10;');
        						    call cats(post, 'drop ____date;');
        						    hasDate = 1;
        						end;

								call cats(row, "n do;response.callStringMethod('getDate', '", column, "', ____date);", trim(column) || " = input(____date, YYMMDD10.);end;");
								call cats(post, "format " || strip(column) || " DATE9.;");
							end;
					    else if (type = 'BOOLEAN') then
					        do;
        						call cats(pre, 'length ' || strip(column) || ' 4;');
					            call cats(row, "n response.callBooleanMethod('getBoolean', '", column, "', ", column, ");");
					        end;
						else
							do;
        						call cats(pre, 'length ' || strip(column) || ' 8;');
								call cats(row, "n response.callDoubleMethod('getNumeric', '", column, "', ", column, ");");
							end;

					    response.callBooleanMethod('allowsMissingValues', column, allowsMissing);

					    if (allowsMissing) then call cats(row, "else do; response.callStringMethod('getMissingValue', '", column, "', missingValue); ", trim(column) || " = input(missingValue, 2.); end;");
					end;

				response.callStringMethod('getLabel', index, labelText);
    		    call cats(pre, "label " || trim(column) || " = '", trim(labelText), "';");

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
			Issue the query again to retrieve the data.
		*/
        %_labkeySendCommand(&selectRows, 0);

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
		drop hasAnother isNull missingValue;

		title &title;

		response.delete();
		command.delete();
		cn.delete();
	run
%mend _labkeySharedSelectRowsHandling;
