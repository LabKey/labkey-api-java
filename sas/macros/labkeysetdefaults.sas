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
	Set default values for commonly used parameters.  Subsequent calls to %labkeySelectRows, %labkeyExecuteSql, etc. will
	use the	values specified here by default.
*/
%macro labkeySetDefaults(baseUrl=, folderPath=, schemaName=, queryName=, userName=, password=, containerFilter=, apikey=);
	%global lk_baseUrl lk_folderPath lk_schemaName lk_queryName lk_userName lk_password lk_apikey;
	%let lk_baseUrl = &baseUrl;
	%let lk_folderPath = &folderPath;
	%let lk_schemaName = &schemaName;
	%let lk_queryName = &queryName;
	%let lk_userName = &userName;
	%let lk_password = &password;
	%let lk_containerFilter = &containerFilter;
	%let lk_apikey = &apikey;
%mend labkeySetDefaults;
