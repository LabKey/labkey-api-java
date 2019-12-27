##The LabKey Remote API Library for Java

Welcome to the LabKey Remote API library for Java. This library allows Java developers to access the data and services exposed from a LabKey Server installation.

For detailed information on the library, its classes, and programming interfaces, see the [JavaDoc](https://www.labkey.org/download/clientapi_docs/java-api/).

If you have any questions about this library or the LabKey Server, see our [Community Support Forums](https://www.labkey.org/wiki/home/page.view?name=support).

This library is licensed under the [Apache 2.0 open-source license](http://www.apache.org/licenses/LICENSE-2.0).

---

###Changes in version 1.0.0
- Migration to [SemVer](https://semver.org/) for library versioning. With 1.0.0 we will move away from using the LabKey server version 
and adopt the standard used in our other API libraries. Version 1.0.0 will succeed the previous published version of : 19.1.0.
- Support for truncating tables, there is a new TruncateTableCommand to expose this functionality.
- CRUD operations for Domains. 
- Improved support for Assay data import. LoadAssayBatchCommand and SaveAssayRunCommand have been added to allow support for ad-hoc properties.
- New ListDomainsCommand.
- New SaveAssayRunsCommand.
- Update PropertyDescriptor to expose missing value toggling. 
