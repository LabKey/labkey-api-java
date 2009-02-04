/*
	Deletes all the SAS/LabKey macros in SASMACR.  Makes it easier to develop autocall macros,
	since modifications can be made and reloaded without constantly restarting SAS.
*/
proc catalog cat=WORK.SASMACR;
	delete executeSql / et=macro;
	delete insertRows / et=macro;
	delete makeFilter / et=macro;
	delete saveRows / et=macro;
	delete selectRows / et=macro;
	delete setDefaults / et=macro;
	delete sharedSelectRowsHandling / et=macro;
quit;
