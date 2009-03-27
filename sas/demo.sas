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
%macro labkeyDemo(url="http://localhost:8080/labkey", folder="/home");
	/*  Simple example specifying all required parameters.  */
	%labkeySelectRows(dsn=all, baseUrl=&url, folderPath=&folder, schemaName="lists", queryName="People");
	proc print label data=all; run;

	/*	Set default parameter values to use in subsequent calls.  */
	%labkeySetDefaults(baseUrl=&url, folderPath=&folder, schemaName="lists", queryName="People");

	/*  Same result as last %labkeySelectRows() call, but only need to specify data set name since defaults are now set. */
	%labkeySelectRows(dsn=all2);
	proc print label data=all2; run;

	/*  These data sets should be identical.  */
	proc compare base=all compare=all2; run;

	/*  Specify two filters: only males less than a certain height. */
	%labkeySelectRows(dsn=shortGuys, filter=%labkeyMakeFilter("Sex", "EQUAL", 1, "Height", "LESS_THAN", 1.2));
	proc print label data=shortGuys; run;

	/*  Demonstrate an IN filter: only people whose age is specified.  */
	%labkeySelectRows(dsn=lateThirties, filter=%labkeyMakeFilter("Age", "EQUALS_ONE_OF", "36;37;38;39"));
	proc print label data=lateThirties; run;

	/*  Specify a view and a not missing filter.  */
	%labkeySelectRows(dsn=namesByAge, viewName="namesByAge", filter=%labkeyMakeFilter("Age", "NOT_MISSING"));
	proc print label data=namesByAge; run;

	/*  Demonstrate column list, sort, row limiting, and row offset.  */
	%labkeySelectRows(dsn=limitRows, columns="First, Last, Age", sort="Last, -First", maxRows=3, rowOffset=1);
	proc print label data=limitRows; run;

	/*  Query using custom SQL... GROUP BY and aggregates in this case.  */
	%labkeyExecuteSql(dsn=groups, sql="SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight,
								 AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last");
	proc print label data=groups; run;

	data children;
		length First Last $25;
		First="Pebbles"; Last="Flintstone"; Appearance = input("1963-02-22", YYMMDD10.); Age = 1; Sex = 2; Height = .5; output;
		First="Bamm-Bamm"; Last="Rubble"; Appearance = input("1963-10-01", YYMMDD10.); Age = 1; Sex = 1; Height = .6; output;
		format Appearance DATE9.;
	run;

	/*  Insert the rows defined in the children data set.  */
	%labkeyInsertRows(dsn=children);

	/*  Select all rows, including newly added.  */
	%labkeySelectRows(dsn=everybody);
	proc print label data=everybody; run;

	/*  Select initial characters (appearing on first episode, September 30, 1960).  */
	%labkeySelectRows(dsn=initial, filter=%labkeyMakeFilter("Appearance", "DATE_EQUAL", "30SEP1960"));
	proc print label data=initial; run;

	/*  Select characters that appeared later.  */
	%labkeySelectRows(dsn=later, filter=%labkeyMakeFilter("Appearance", "GREATER_THAN", "30SEP1960"));
	proc print label data=later; run;

	/*  Select a subset of columns (including the key), calculate a new column, and update the column on the server.  */
	%labkeySelectRows(dsn=modify, columns="Appearance, Age", showHidden=1);
	proc print label data=modify; run;

	data updateTest;
		set modify;

		AgeToday = round((today() - Appearance)/365 + Age);

		keep Key AgeToday;
		format AgeToday 3.;
	run;

	proc print label data=updateTest; run;

	%labkeyUpdateRows(dsn=updateTest);

	/*  Select result including updates.  */
	%labkeySelectRows(dsn=updated);
	proc print label data=updated; run;

	/*  Demonstrate UNION between two different data sets.  */
	%labkeyExecuteSql(dsn=combined, sql="SELECT MorePeople.First, MorePeople.Last FROM MorePeople UNION
							   SELECT People.First, People.Last FROM People ORDER BY 2"); 
	proc print label data=combined; run;

	/*  Clean up -- clear the AgeToday column and delete Pebbles & Bamm-Bamm rows  */
	data clearAgeToday;
		set modify;
		call missing(AgeToday);
		keep Key AgeToday;
	run;

	proc print label data=clearAgeToday; run;

	%labkeyUpdateRows(dsn=clearAgeToday);

	%labkeySelectRows(dsn=babies, columns="Key", filter=%labkeyMakeFilter("Age", "LESS_THAN_OR_EQUAL", 2), showHidden=1);
	%labkeyDeleteRows(dsn=babies);

	/*  Data set after clean up should be equivalent to first query.  */
	%labkeySelectRows(dsn=all3);
	proc print label data=all3; run;

	/*  These data sets should be identical.  */
	proc compare base=all compare=all3; run;
%mend labkeyDemo;

options mprint;

%labkeyDemo();

