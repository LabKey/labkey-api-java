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
	Standard code to create a connection:

	- If userName was passed in then use it.
	- Otherwise, if lk_userName is defined (via %setDefaults), use it.
	- Otherwise, if apiKey was passed then use it. **NYI** See #28305
	- Otherwise, if lk_apiKey is defined (via %setDefaults), use it. **NYI** See #28305
	- Otherwise, rely on .netrc handling.
*/
%macro _labkeyCreateConnection();
		%if (&userName eq) and %symexist(lk_userName) %then
		    %do;
                %let userName = &lk_userName;
                %let password = &lk_password;
		    %end;

        %if &userName ne %then
            %do;
                declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &baseUrl, &userName, &password);
            %end;
        %else
            %do;
                declare javaobj cn ('org/labkey/remoteapi/sas/SASConnection', &baseUrl);
            %end;

        %_labkeyExceptionDescribe(cn);
%mend _labkeyCreateConnection;

