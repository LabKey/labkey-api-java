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
	Set all the parameters, initialize the connection, and execute the appropriate command.   If this is Handles maxRows, rowOffset, and containerFilter
	params (if present), initializes the connection, executes the command, handles the meta data, sets a default title,
	and creates a SAS data set containing all the data rows.
*/
%macro _labkeySendCommand(select, metaDataOnly);
    %if (&select) %then %do;
		declare javaobj command ('org/labkey/remoteapi/sas/SASSelectRowsCommand', &schemaName, &queryName);
        %_labkeyExceptionDescribe(command);

		/*
			If viewName, filter, columns, or sort params have been specified then set them on the command
		*/
		%if &viewName ne %then %do;
			command.callVoidMethod('setViewName', &viewName);
		%end;

		%if &filter ne %then %do;
			&filter;
		%end;

		%if &columns ne %then %do;
			command.callVoidMethod('setColumns', &columns);
		%end;

		%if &sort ne %then %do;
			command.callVoidMethod('setSorts', &sort);
		%end;
    %end;
    %else %do;
        declare javaobj command ('org/labkey/remoteapi/sas/SASExecuteSqlCommand', &schemaName, &sql);
        %_labkeyExceptionDescribe(command);
    %end;

    %if (&metaDataOnly) %then %do;
		command.callVoidMethod('setMaxRows', 1);
	%end;
	%else %do;
		/*
			Set maxRows and rowOffset if specified and not meta-data only.
		*/
		%if &maxRows ne %then %do;
			command.callVoidMethod('setMaxRows', &maxRows);
		%end;

		%if &rowOffset ne %then %do;
			command.callVoidMethod('setOffset', &rowOffset);
		%end;
    %end;

    /*
        Container filter is totally optional, so we check here instead of setting containerFilter = &lk_containerFilter in the params list.
    */
    %if (&containerFilter eq) and %symexist(lk_containerFilter) %then %let containerFilter = &lk_containerFilter;

    %if &containerFilter ne %then %do;
        command.callVoidMethod('setContainerFilter', &containerFilter);
    %end;

		/*
			Create the connection, issue the command, and retrieve the response.
		*/
        %_labkeyCreateConnection();

		declare javaobj response('org/labkey/remoteapi/sas/SASSelectRowsResponse', cn, command, &folderPath);
        %_labkeyExceptionDescribe(response);
%mend _labkeySendCommand;
