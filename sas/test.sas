options mprint;

/* Simple example specifying all required parameters. */

%selectRows(baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="Lists", queryName="People", dsn=all);

proc print;
run;

/*	Set default parameter values to use in all subsequent calls. */

%setDefaults(baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="Lists", queryName="People");

/*  Same result as %selectRows() above call, but only need to specify data set name since defaults have been set. */

%selectRows(dsn=all2);

proc print;
run;

/*  Demonstrate a filter: only males less than a certain height. */

%selectRows(filter=%makeFilter("Sex", "EQUALS", 1, "Height", "LESS_THAN", 1.7), dsn=shortMales);

proc print;
run;

/*  Specify a view. */

%selectRows(viewName="namesByAge", dsn=namesByAge);

proc print;
run;

/*  Demostrate column list, sort, row limiting, row offset, and showing key columns. */

%selectRows(colSelect="First, Last, Age", colSort="Last, -First", maxRows=3, rowOffset=1, showHidden=1, dsn=limitRows);

proc print;
run;

/*  Query using arbitrary SQL. */

%executeSql(sql="SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight, AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last", dsn=groups);

proc print;
run;
/*
%selectRows(baseUrl="https://atlas.scharp.org/cpas", folderPath="/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project", schemaName='study', queryName='Monogram NAb', colSelect="ConcentrationValue, PercentInhibition", dsn=nab);

proc print;
run;
*/

/* Pebbles: February 22, 1963 */
/* Bamm-Bamm: October 1, 1963 */
