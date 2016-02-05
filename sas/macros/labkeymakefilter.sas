/*
 * Copyright (c) 2009-2016 LabKey Corporation
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
	Creates a filter to pass as the %labkeySelectRows filter= parameter.  Multiple filters can be specified.
	The MISSING and NOT_MISSING operators don't require a value; all other operators require a value.

	The string generated by this macro consists of calls to the command.addFilter() methods.
*/
%macro labkeyMakeFilter/parmbuff;
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

		%if %index("EQUAL" "DATE_EQUAL"
		           "NOT_EQUAL" "DATE_NOT_EQUAL"
		           "NOT_EQUAL_OR_MISSING"
		           "GT" "GREATER_THAN" "DATE_GT" "DATE_GREATER_THAN"
		           "LT" "LESS_THAN" "DATE_LT" "DATE_LESS_THAN"
		           "GTE" "GREATER_THAN_OR_EQUAL" "DATE_GTE" "DATE_GREATER_THAN_OR_EQUAL"
		           "LTE" "LESS_THAN_OR_EQUAL" "DATE_LTE" "DATE_LESS_THAN_OR_EQUAL"
                   "STARTS_WITH" "DOES_NOT_START_WITH"
                   "CONTAINS" "DOES_NOT_CONTAIN"
                   "CONTAINS_ONE_OF" "CONTAINS_NONE_OF"
                   "IN" "NOT_IN"
                   "BETWEEN" "NOT_BETWEEN"
                   "MEMBER_OF" &operator) %then
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
				%let code = &code command.callVoidMethod('addFilter', &column, &operator, &value)%str(;);
			%end;
		%else
			%do;
				%if %index("MISSING" "NOT_MISSING" "MV_INDICATOR" "NO_MV_INDICATOR", &operator) %then
					%do;
					/*
						For operators that don't require a value, just set the column and operator.
					*/
						%let code = &code command.callVoidMethod('addFilter', &column, &operator)%str(;);
					%end;
				%else
					%do;
						%put ERROR: Invalid operator: &operator;
						%abort;
					%end;
			%end;
		
   		%let num = %eval(&num + 1);
		%let column = %scan(&params, &num, %str( ,));
	%end;
	%quote(&code);
%mend labkeyMakeFilter;
