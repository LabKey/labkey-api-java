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
	Deletes all the SAS/LabKey macros in SASMACR.  Makes it easier to develop autocall macros,
	since modifications can be made and reloaded without constantly restarting SAS.
*/
proc catalog cat=WORK.SASMACR;
	delete labkeyExecuteSql / et=macro;
	delete labkeyInsertRows / et=macro;
	delete labkeyMakeFilter / et=macro;
	delete labkeySelectRows / et=macro;
	delete labkeySetDefaults / et=macro;

	delete _labkeySaveRows / et=macro;
	delete _labkeySharedSelectRowsHandling / et=macro;
	delete _labkeyCreateConnection / et=macro;
	delete _labkeySendCommand / et=macro;
	delete _labkeyExceptionDescribe / et=macro;
quit;
