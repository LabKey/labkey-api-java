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
options mprint;

/*  Simple example specifying all required parameters.  */
%labkeySelectRows(dsn=all, baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="lists", queryName="People");
proc print data=all; run;

/*	Set default parameter values to use in subsequent calls.  */
%labkeySetDefaults(baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="lists", queryName="People");

/*  Same result as last %labkeySelectRows() call, but only need to specify data set name since defaults are now set. */
%labkeySelectRows(dsn=all2);
proc print data=all2; run;

/*  These data sets should be identical.  */
proc compare base=all compare=all2; run;

/*  Specify two filters: only males less than a certain height. */
%labkeySelectRows(dsn=shortGuys, filter=%labkeyMakeFilter("Sex", "EQUALS", 1, "Height", "LESS_THAN", 1.2));
proc print data=shortGuys; run;

/*  Demonstrate an IN filter: only people whose age is specified.  */
%labkeySelectRows(dsn=lateThirties, filter=%labkeyMakeFilter("Age", "EQUALS_ONE_OF", "36;37;38;39"));
proc print data=lateThirties; run;

/*  Specify a view and a not missing filter.  */
%labkeySelectRows(dsn=namesByAge, viewName="namesByAge", filter=%labkeyMakeFilter("Age", "IS_NOT_MISSING"));
proc print data=namesByAge; run;

/*  Demonstrate column list, sort, row limiting, and row offset.  */
%labkeySelectRows(dsn=limitRows, colSelect="First, Last, Age", colSort="Last, -First", maxRows=3, rowOffset=1);
proc print data=limitRows; run;

/*  Query using custom SQL... GROUP BY and aggregates in this case.  */
%labkeyExecuteSql(dsn=groups, sql="SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight,
							 AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last");
proc print data=groups; run;

data children;
	input First : $25. Last : $25. Appearance : mmddyy10. Age Sex Height ;
	format Appearance DATE9.;
	datalines;
Pebbles Flintstone 022263 1 2 .5
Bamm-Bamm Rubble 100163 1 1 .6
;

/*  Insert the rows defined in the children data set.  */
%labkeyInsertRows(dsn=children);

/*  Select all rows, including newly added.  */
%labkeySelectRows(dsn=everybody);
proc print data=everybody; run;

/*  Select a subset of columns (including the key), calculate a new column, and update the column on the server.  */
%labkeySelectRows(dsn=modify, colSelect="Appearance, Age", showHidden=1);
proc print data=modify; run;

data updateTest;
	set modify;

	AgeToday = round((today() - Appearance)/365 + Age);

	keep Key AgeToday;
	format AgeToday 3.;
run;

proc print data=updateTest; run;

%labkeyUpdateRows(dsn=updateTest);

/*  Select result including updates.  */
%labkeySelectRows(dsn=updated);
proc print data=updated; run;

/* Demonstrate UNION between two different data sets.  */
%labkeyExecuteSql(dsn=combined, sql="SELECT MorePeople.First, MorePeople.Last FROM MorePeople UNION
						   SELECT People.First, People.Last FROM People ORDER BY 2"); 
proc print data=combined; run;

/*  Clean up -- clear the AgeToday column and delete Pebbles & Bamm-Bamm rows  */
data clearAgeToday;
	set modify;
	call missing(AgeToday);
	keep Key AgeToday;
run;

proc print data=clearAgeToday; run;

%labkeyUpdateRows(dsn=clearAgeToday);

%labkeySelectRows(dsn=babies, colSelect="Key", filter=%labkeyMakeFilter("Age", "LESS_THAN_OR_EQUAL_TO", 2), showHidden=1);
%labkeyDeleteRows(dsn=babies);
