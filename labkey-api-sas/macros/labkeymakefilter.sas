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

	Quote column names and values with single quotes: SAS resolves & and % inside double quotes before this
	macro runs. Embed a single quote by doubling it. Quoted values may contain spaces and commas.

	The macro saves its arguments unresolved in a global macro variable. The generated data step code reads
	them back with SYMGET, parses them, and calls command.addFilter() for each filter.
*/
%macro labkeyMakeFilter/parmbuff;
	%global _lk_filter_count;
	%local name;

	/*
		A distinct variable per call keeps filters that are saved in macro variables separate
	*/
	%let _lk_filter_count = %eval(0&_lk_filter_count + 1);
	%let name = _lk_filter&_lk_filter_count;
	%global &name;
	%let &name = %superq(syspbuff);

	%nrstr(length _lk_args _lk_column _lk_operator _lk_value $32767; drop _lk_:;)
	%nrstr(_lk_args = symget%(%')&name%nrstr(%'%);)
	%nrstr(
		_lk_args = strip(_lk_args);
		_lk_args = substrn(_lk_args, 2, length(_lk_args) - 2);
		_lk_i = 1;
		_lk_column = dequote(scan(_lk_args, _lk_i, ' ,', 'q'));

		do while (not missing(_lk_column));
			_lk_operator = upcase(dequote(scan(_lk_args, _lk_i + 1, ' ,', 'q')));

			if indexw('MISSING NOT_MISSING MV_INDICATOR NO_MV_INDICATOR', strip(_lk_operator)) then do;
				command.callVoidMethod('addFilter', strip(_lk_column), strip(_lk_operator));
				_lk_i = _lk_i + 2;
			end;
			else if indexw('EQUAL DATE_EQUAL NOT_EQUAL DATE_NOT_EQUAL NOT_EQUAL_OR_MISSING GT GREATER_THAN DATE_GT DATE_GREATER_THAN LT LESS_THAN DATE_LT DATE_LESS_THAN GTE GREATER_THAN_OR_EQUAL DATE_GTE DATE_GREATER_THAN_OR_EQUAL LTE LESS_THAN_OR_EQUAL DATE_LTE DATE_LESS_THAN_OR_EQUAL STARTS_WITH DOES_NOT_START_WITH CONTAINS DOES_NOT_CONTAIN CONTAINS_ONE_OF CONTAINS_NONE_OF IN NOT_IN BETWEEN NOT_BETWEEN Q WHERE MEMBER_OF', strip(_lk_operator)) then do;
				/*
					Values are always sent as strings; the server chokes on a double value for an integer column
				*/
				_lk_value = dequote(scan(_lk_args, _lk_i + 2, ' ,', 'q'));
				command.callVoidMethod('addFilter', strip(_lk_column), strip(_lk_operator), trim(_lk_value));
				_lk_i = _lk_i + 3;
			end;
			else do;
				put 'ERROR: Invalid operator: ' _lk_operator;
				abort;
			end;

			_lk_column = dequote(scan(_lk_args, _lk_i, ' ,', 'q'));
		end;
	)
%mend labkeyMakeFilter;
