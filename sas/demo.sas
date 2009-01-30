options mprint;

/* Simple example specifying all required parameters.  */
%selectRows(dsn=all, baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="Lists", queryName="People");

proc print data=all; run;

/*	Set default parameter values to use in subsequent calls.  */
%setDefaults(baseUrl="http://localhost:8080/labkey", folderPath="/home", schemaName="Lists", queryName="People");

/*  Same result as last %selectRows() call, but only need to specify data set name since defaults are now set. */
%selectRows(dsn=all2);

proc print data=all2; run;

/*  These data sets should be identical.  */
proc compare base=all compare=all2; run;

/*  Specify two filters: only males less than a certain height. */
%selectRows(dsn=shortMales, filter=%makeFilter("Sex", "EQUALS", "1", "Height", "LESS_THAN", 1.7));

proc print data=shortMales; run;

/*  Demonstrate an IN filter: only people whose age is specified.  */
%selectRows(dsn=lateThirties, filter=%makeFilter("Age", "EQUALS_ONE_OF", "36;37;38;39"));

proc print data=lateThirties; run;

/*  Specify a view and a not missing filter.  */
%selectRows(dsn=namesByAge, viewName="namesByAge", filter=%makeFilter("Age", "IS_NOT_MISSING"));

proc print data=namesByAge; run;

/*  Demostrate column list, sort, row limiting, row offset, and displaying key columns.  */
%selectRows(dsn=limitRows, colSelect="First, Last, Age", colSort="Last, -First", maxRows=3, rowOffset=1, showHidden=1);

proc print data=limitRows; run;

/*  Query using custom SQL -- GROUP BY and aggregates in this case.  */
%executeSql(dsn=groups, sql="SELECT People.Last, COUNT(People.First) AS Number, AVG(People.Height) AS AverageHeight, AVG(People.Age) AS AverageAge FROM People GROUP BY People.Last");

proc print data=groups; run;

%selectRows(dsn=nab, baseUrl="https://atlas.scharp.org/cpas", folderPath="/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project", schemaName="study", queryName="Monogram NAb", colSelect="ConcentrationValue, PercentInhibition");

proc print data=nab; run;

/* Pebbles: February 22, 1963 */
/* Bamm-Bamm: October 1, 1963 */
