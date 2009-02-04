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
	Deletes rows in the specified schema and query based on the keys in the specified data set.
*/
%macro deleteRows(baseUrl=&lk_baseUrl, folderPath=&lk_folderPath, schemaName=&lk_schemaName, queryName=&lk_queryName, dsn=);
    %saveRows(SASDeleteRowsCommand, deleted, &baseUrl, &folderPath, &schemaName, &queryName, &dsn);
%mend deleteRows;

