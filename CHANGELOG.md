# The LabKey Remote API Library for Java - Change Log

## version 6.0.0-SNAPSHOT
*Released*: TBD
* Obfuscate SQL parameters passed by `ExecuteSqlCommand` and `SqlExecuteCommand` to avoid rejection by web application firewalls
  * Earliest compatible LabKey Server version: 23.9.0
  * These commands are no longer compatible with earlier versions of LabKey Server (23.8.x and before) by default, however,
    if targeting an older server, calling `ExecuteSqlCommand.setWafEncoding(false)` will restore the previous behavior.
* Update HttpCore, JSON-java, Gradle Plugins, and Gradle versions

## version 5.3.0
*Released*: 7 September 2023
* Update to Gradle 8.3 and adjust away from deprecated syntax
* Add Ontology and Lineage filter type operators
* Add `TotpManager`, a simple helper that generates TOTP one-time passwords
* Update Commons Codec, HttpCore, and Gradle Plugins versions

## version 5.2.0
*Released*: 3 May 2023
* Add `RenameContainerCommand` and `RenameContainerResponse`

## version 5.1.0
*Released*: 3 March 2023
* Delegate first request behavior to the configured `CredentialsProvider`. Connection-based providers invoke
  `login-ensureLogin.api` and connection-less providers invoke `login-whoAmI.api`.
* Restore connection-based authentication for `BasicCredentialsProvider`
* Add logging to `NetrcFileParser` to help with debugging authentication problems
* Optional parameters to `GetContainersCommand` to reduce the size of the response payload
* Upgrade to most recent JSON-java, Gradle, and Gradle Plugins versions

## version 5.0.1
*Released*: 30 January 2023
* Fix regression introduced in 5.0.0: `RowsResponse.fixupParsedData()` was called before `_requiredVersion` was set. This caused
  the fixup method to skip `BigDecimal` to `Double` conversions in the returned data maps.

## version 5.0.0
*Released*: 24 January 2023
* Refactor the `Command` class hierarchy:
  * Add `GetCommand`, new abstract base class for all commands that use get
  * Make `Command` and `PostCommand` abstract
  * Add `SimpleGetCommand`, new concrete class used (instead of `Command`) to invoke GET API actions without creating a subclass
  * Add `SimplePostCommand`, new concrete class used (instead of `PostCommand`) to invoke POST API actions without creating a subclass
* Refactor parameter handling for consistency:
  * `createParameterMap()` is now used by subclasses to create and populate a mutable map of URL parameters
  * `getParameters()` now returns an immutable copy of the current URL parameter map for external use, typically logging or testing
  * Commands no longer stash and reuse the parameter map; `createParameterMap()` always creates a new map.
  * `setParameters()` is now available only on `SimpleGetCommand` and `SimplePostCommand`. Custom `GetCommand` and `PostCommand`
    subclasses that need to specify parameters are expected to override `createParameterMap()`.
  * `setJsonObject()` is now available only on `SimplePostCommand`. Custom `PostCommand` subclasses that need to post JSON are
    expected to override `getJsonObject()`.
* Stop passing command subclasses when constructing every `CommandResponse`. The two response classes that need this now implement
  it without burdening all other commands. 
* Introduce `HasRequiredVersion` interface and use it when instantiating `CommandResponse` subclasses that need required version
* Remove all `Command` copy constructors. Same rationale as the earlier removal of `copy` methods.
* Switch `SelectRowsCommand` and `NAbRunsCommand` to post their parameters as JSON
* Fix `NAbReplicate` to handle `"NaN"` values
* Remove `CommandException` from `getHttpRequest()` throws list
* Adjust the `Demo.java` and `Test.java` tests to match current sample data and `Command` hierarchy changes

## version 4.3.1
*Released*: 15 January 2023
* Fix regression introduced in 4.3.0: `SelectRowsCommand` should create a fresh parameter map for every invocation of `getParameters()`

## version 4.3.0
*Released*: 11 January 2023
* [Issue 47030](https://www.labkey.org/home/Developer/issues/issues-details.view?issueId=47030): Switch `SelectRowsCommand`
  and `NAbRunsCommand` to always use POST
* Add support for `includeTotalCount`, `includeMetadata`, and `ignoreFilter` flags to `BaseQueryCommand` and reconcile 
  duplicate parameter handling code vs. SelectRowsCommand
* Add support for `includeTitle` and `includeViewDataUrl` flags to `GetQueriesCommand`

## version 4.2.0
*Released*: 10 January 2023
* Refactor repository layout, removing unnecessary `labkey-client-api` folder
* Upgrade to most recent Gradle, Gradle Plugins, and HttpClient versions

## version 4.1.0
*Released*: 15 December 2022
* Migrate internal HTTP handling to use Apache HttpClient 5.2
* Generate and execute path-first URLs
* Add PrimaryStorage a new StorageItemTypes for non-freezer storage

## version 4.0.0
*Released*: 26 October 2022
* Migrate to a new JSON library: [JSON-java](https://github.com/stleary/JSON-java). The previous library,
  [json-simple](https://github.com/fangyidong/json-simple) is no longer maintained (last released in early 2012) and
  lacks support for basic features like generics. This is an incompatible API change for developers who *write* their own
  Command classes; they will need to update their Command classes if/when they upgrade to v4.0.0. Developers who simply
  *use* Command classes should be able to upgrade without changes.
* API changes:
  * Remove `copy` method from Commands. _It was inconsistently implemented and served little purpose._
  * Remove `CommandResponse.getSourceCommand`. _Functionality would vary wildly because of inconsistent `Command.copy` implementations._
  * Make the properties stashed by `ResponseObject` immutable.
* Issue 46321: Remove `lib` directory from `fatJar` in favor of pulling dependencies via the published pom files when needed
* Remove artifactory plugin since we use the maven `publish` command now

## version 3.1.0
*Released*: 20 September 2022
* Add support for creating Freezer Manager freezer hierarchies via StorageController APIs (earliest compatible LabKey Server version: 22.10.0)
  * CreateCommand, UpdateCommand, DeleteCommand
* Restore proactive authentication behavior. A change in v3.0.0 caused some invocations of `@NoPermissionsRequired`
  actions (e.g., `GetContainersCommand`) to use guest credentials instead of the configured user credentials. The library
  now always authenticates using the configured credentials, matching pre-v3.0.0 behavior.

## version 3.0.0
*Released*: 14 September 2022
* Migrate internal HTTP handling to use Apache HttpClient 5.1.x
* Migrate from preemptive to challenge-response Basic authentication 
* Switch `StopImpersonatingCommand` to disable redirects (mimicking previous behavior)
* Add `Connection.stopImpersonating()` and deprecate `stopImpersonate()`
* Remove deprecated methods:
  * ApiVersionException() (use constructor that takes contentType)
  * CommandException() (use constructor that takes contentType)
  * Connection.getBaseUrl() (use Connection.getBaseURI())
  * CredentialsProvider.configureRequest() (use variant that takes a URI)
  * Filter.NON_BLANK (use Filter.NONBLANK)
  * Filter.getCaption() (use Filter.getDisplayValue())
  * Filter.getName() (use Filter.getUrlKey())
  * Filter.isDataValueRequired() (use Filter.isValueRequired())
* Remove SAS macros and wrapper classes
* Add `CreateFolderCommand`
* Add `CreateProjectCommand` (earliest compatible LabKey Server version: 22.3.0)
* Update `LogoutCommand` to use POST

## version 2.0.0
*Released*: 25 July 2022
* Require Java 17 to build and run

## version 1.5.2
*Released*: 14 July 2022
* Add Connection.setUserAgent() and Connection.getUserAgent()
* Set Java library user agent to "LabKey Java API"
* Set SAS library user agent to "LabKey SAS API"

## version 1.5.1
*Released*: 7 July 2022
* Fix NPE when saving assay protocol with transform scripts
* Add derivationDataScope to PropertyDescriptor

## version 1.5.0
*Released*: 20 April 2022
* Update gradle and various dependencies
* Update signature of `Connection` constructor
* [Issue 43380](https://www.labkey.org/home/Developer/issues/issues-details.view?issueId=43380): `ImportDataCommand` missing options supported by the query-import.api endpoint
* Remove `CheckForStudyReloadCommand`.

## version 1.4.0
*Released*: 16 June 2021
* [Issue 43246](https://www.labkey.org/home/Developer/issues/issues-details.view?issueId=43246): Lineage query NPE while processing an UploadedFile
* Additional lineage options and support additional properties in response
* Update dependency version numbers
* Update to Gradle 7.1

## version 1.3.2
*Released* : 05 November 2020
* Fix `selectedMetadataInputFormat` serialization
* Add some missing dependency declarations 

## version 1.3.1
*Released* : 18 September 2020

* Fix pre-population of session ID and CSRF token in Connection
* Identify target server with a `URI` instead of a `String`
* Add support for Log4J 2

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
