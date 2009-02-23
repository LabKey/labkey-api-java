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
quit;
