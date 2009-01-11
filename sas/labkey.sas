%macro setConnection(baseUrl);
	%global lk_url;
	%let lk_url = &baseUrl;
%mend;

%macro selectRows(folder, schema, query, dataSetName, view=, columns=, sort=, offset=, maxRows=);
	data _null_;
		declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &lk_url);
		declare javaobj sasSelectRows ('org/labkey/remoteapi/sas/SASSelectRowsCommand', &schema, &query);

		length title $1000;

		title = "Schema: &schema, Query: &query";

		%if &view ne %then %do;
			sasSelectRows.callVoidMethod('setViewName', &view);
			call cats(title, ", View: &view");
		%end;

		%if &columns ne %then %do;
			sasSelectRows.callVoidMethod('setColumns', &columns);
		%end;

		%if &sort ne %then	%do;
			sasSelectRows.callVoidMethod('setSorts', &sort);
		%end;

		%if &maxRows ne %then %do;
			sasSelectRows.callVoidMethod('setMaxRows', &maxRows);
		%end;

		%if &offset ne %then %do;
			sasSelectRows.callVoidMethod('setOffset', &offset);
		%end;

		declare javaobj sasResponse ('org/labkey/remoteapi/sas/SASResponse', cn, sasSelectRows, &folder);

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

		call symput('preCode', pre);
		call symput('rowCode', row);
		call symput('postCode', post);
		call symput('title', quote(strip(title)));

		length key $2;
	
		sasResponse.callStringMethod('cache', key);

		call symput('key', key);

		sasResponse.delete();
		sasSelectRows.delete();
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
%mend;

options mprint;

%setConnection('http://localhost:8080/labkey');

%selectRows('home', 'Lists', 'People', all);

proc print;
run;

%selectRows('home', 'Lists', 'People', subset, view='namesByAge', columns='First, Last', sort='Last, -First', maxRows=3, offset=1);

proc print;
run;

%setConnection('https://atlas.scharp.org/cpas');

%selectRows('/VISC/Zolla-Pazner-VDC/Neut Data Analysis Project', 'study', 'Monogram NAb', nab, columns='ConcentrationValue, PercentInhibition');

proc print;
run;

