# The LabKey Remote API Library for Java

Welcome to the LabKey Remote API library for Java. This library allows Java 
developers to access the data and services exposed from a LabKey Server installation.

For more information on the library, 
see our [JavaAPI](https://www.labkey.org/Documentation/wiki-page.view?name=javaAPI) page, 
which includes a link to the [JavaDoc](https://www.labkey.org/download/clientapi_docs/java-api/)
where you can get details about the classes and programming interfaces,. 

If you have any questions about this library or the LabKey Server, 
see our [Community Support Forums](https://www.labkey.org/home/Support/LabKey%20Support%20Forum/project-begin.view?).

This library is licensed under the [Apache 2.0 open-source license](http://www.apache.org/licenses/LICENSE-2.0).

---
## Release Notes
## version TBD
*Released* : TBD
*Earliest Compatible LabKey Version*:

* Update FileNotificationCommand to do a POST, as now required

## version 1.2.0
*Released*: 16 April 2020
*Earliest Compatible LabKey Version*: TBD

* small fixes to LABKEY.Experiment APIs
* Support impersonation via Java client API

## version 1.1.0
*Released*: 19 March 2020
*Earliest Compatible LabKey Version*: TBD

* Support for plate metadata in saveBatch and importRun APIs
* Don't post invalid 'rangeURI' to server
* Update source compatability to 1.8

## version 1.0.0
*Released*: 06 February 2020
*Earliest Compatible LabKey Version*: TBD

- Migration to [SemVer](https://semver.org/) for library versioning. With 1.0.0 we will move away from using the LabKey server version 
and adopt the standard used in our other API libraries. Version 1.0.0 will succeed the previous published version of : 19.1.0.
- Support for truncating tables, there is a new TruncateTableCommand to expose this functionality.
- CRUD operations for Domains. 
- Improved support for Assay data import. LoadAssayBatchCommand and SaveAssayRunCommand have been added to allow support for ad-hoc properties.
- New ListDomainsCommand.
- New SaveAssayRunsCommand.
- Update PropertyDescriptor to expose missing value toggling. 
