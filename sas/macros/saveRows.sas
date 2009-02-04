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
	Initializes a SAS command object with data set observations and executes the command.  Used by %insertRows,
	%updateRows, and %deleteRows.
*/
%macro saveRows(commandClass, verb, baseUrl, folderPath, schemaName, queryName, dsn);
	data _null_;
        set &dsn;

        /*
            Create the command on the first observation.
        */
		if _N_ = 1 then do;
		    declare javaobj command ("org/labkey/remoteapi/sas/&commandClass", &schemaName, &queryName);
		end;

        /*
            Create a new row.
        */
        declare javaobj row ('org/labkey/remoteapi/sas/SASRow');

        /*
            Determine the number of observations and output the code that puts the observation values into the row.
        */
        %let dsid = %sysfunc(open(&dsn, i));
        %let nobs = %sysfunc(attrn(&dsid, nobs));

        %do i = 1 %to %sysfunc(attrn(&dsid, nvars));
            %let name = %sysfunc(varname(&dsid, &i));
            %let type = %sysfunc(vartype(&dsid, &i));

            %if (&type = N) %then
                %do;
                    %let fmt = %sysfunc(varfmt(&dsid, &i));
                    %put &name " has format " &fmt;

                    /* TODO: also look for DDMMYY and other variants */
                    %if %index("DATE9." "DATE7." "MMDDYY10." "MMDDYY8." "WORDDATE18." "WEEKDATE29.", "&fmt") %then
                        %do;
                            row.callVoidMethod("putDate", "&name", &name);
                        %end;
                    %else
                        %do;
                            row.callVoidMethod("put", "&name", &name);
                        %end;
                %end;
            %else
                %do;
                    row.callVoidMethod("put", "&name", &name);
                %end;
        %end;

        %let ret = %sysfunc(close(&dsid));

        /*
            Add the row to the command.
        */
        command.callVoidMethod('addRow', row);

		/*
			If we just handled the last observation, create the connection, issue the command, and retrieve the response.
		*/
		if _N_ = &nobs then do;
            declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &baseUrl);
            declare javaobj response ('org/labkey/remoteapi/sas/SASSaveRowsResponse', cn, command, &folderPath);

            response.callIntMethod('getRowsAffected', columnCount);

            put columnCount "rows were &verb";

            response.delete();
            cn.delete();
            row.delete();
            command.delete();
        end;
	run;
%mend saveRows;

