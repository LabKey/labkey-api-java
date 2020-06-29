# The LabKey Remote API Library for Java - Change Log

## version 1.3.1 (TBD)
*Released* : TBD

* Fix pre-population of session and CSRF token

## version 1.3.0
*Released* : 16 June 2020

* 7368: Conditional Formats of Fields
* Update FileNotificationCommand to do a POST, as now required
* Add documentation

## version 1.2.0
*Released*: 16 April 2020

* small fixes to LABKEY.Experiment APIs
* Support impersonation via Java client API

## version 1.1.0
*Released*: 19 March 2020

* Support for plate metadata in saveBatch and importRun APIs
* Don't post invalid 'rangeURI' to server
* Update source compatibility to 1.8

## version 1.0.0
*Released*: 06 February 2020

- Migration to [SemVer](https://semver.org/) for library versioning. With 1.0.0 we will move away from using the LabKey server version 
and adopt the standard used in our other API libraries. Version 1.0.0 will succeed the previous published version of : 19.1.0.
- Support for truncating tables, there is a new TruncateTableCommand to expose this functionality.
- CRUD operations for Domains. 
- Improved support for Assay data import. LoadAssayBatchCommand and SaveAssayRunCommand have been added to allow support for ad-hoc properties.
- New ListDomainsCommand.
- New SaveAssayRunsCommand.
- Update PropertyDescriptor to expose missing value toggling. 
