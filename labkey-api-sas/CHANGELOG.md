# The LabKey Remote API Library for SAS - Change Log

## version 1.1.0
*Released* : TBD
* `%labkeyMakeFilter` accepts every operator that takes a value; previously all of them failed ([GH Issue 1800](https://github.com/LabKey/internal-issues/issues/1800))
* `%labkeyMakeFilter` keeps quotes, spaces, and commas in quoted values instead of stripping or splitting on them
* `%labkeyMakeFilter` no longer resolves `&` or `%` in single-quoted arguments; quote column names and values with single quotes
* `%labkeyMakeFilter` reports an invalid operator when the data step runs rather than when the macro is called
* Untested against a live SAS installation; verify before release

## version 1.0.1
*Released* : 26 July 2022
* Update labkeyClientApi to v1.5.2

## version 1.0.0
*Released* : 5 June 2020

* Initial publication using [SemVer](https://semver.org/) for library versioning.
  With 1.0.0, we will move away from using the LabKey server version 
  and adopt the standard used in our other API libraries. 
  Version 1.0.0 will be published as `org.labkey.api:labkey-api-sas:1.0.0`.

