# The LabKey Remote API Library for Java

Welcome to the LabKey Remote API library for Java. This library allows Java 
developers to access the data and services exposed from a LabKey Server installation.

For more information on the library, 
see our [JavaAPI](https://www.labkey.org/Documentation/wiki-page.view?name=javaAPI) page, 
which includes a link to the [JavaDoc](https://www.labkey.org/download/clientapi_docs/java-api/)
where you can get details about the classes and programming interfaces. 

If you have any questions about this library or the LabKey Server, 
see our [Community Support Forums](https://www.labkey.org/home/Support/LabKey%20Support%20Forum/project-begin.view?).

This library is licensed under the [Apache 2.0 open-source license](http://www.apache.org/licenses/LICENSE-2.0).

See the [change log](CHANGELOG.md) for information on the release versions and their
compatibility with LabKey Server versions.

## Development

### Dependency Declaration
To declare a dependency on this jar file, you can use the following in Gradle

```compile(group: 'org.labkey.api', name: 'labkey-client-api', version: '1.2.0')```

If using the LabKey Gradle plugins and building a LabKey module, it is best to 
use this utility method instead to facilitate testing of any local changes to
this jar file with that module
```
BuildUtils.addLabKeyDependency(project: project, config: "remoteApi", depProjectPath: BuildUtils.getRemoteApiProjectPath(gradle), depVersion: project.labkeyClientApiVersion)
```

### Making Changes

Though this repository contains its own Gradle version and settings file and can be built
independently of the rest of LabKey server, you can also include it in the larger LabKey 
Server Gradle project if you need to make changes to the code and test them locally without
having to publish.  

- Create a feature branch of this repository (`fb_myNewFeature`)
- Clone this repository (`labkey-api-java`) into the `$LABKEY_ROOT/remoteapi` directory
- Update the version in the appropriate `build.gradle` file to a unique SNAPSHOT version. Make sure 
it is a version number not in use by another branch, including `develop`.  A good practice to 
follow is to use your feature branch in the naming of that version (e.g., `X.Y.Z-myNewFeature-SNAPSHOT`). 
TeamCity will automatically publish new SNAPSHOT versions from feature branches.
- Update your `$LABKEY_ROOT/settings.gradle` file to include this project (`include ':remoteapi:labkey-api-java:java'`)

After making these changes, in all places where a dependency on the Java API has been declared like so: 

`BuildUtils.addLabKeyDependency(project: project, config: "remoteApi", depProjectPath: BuildUtils.getRemoteApiProjectPath(gradle), depVersion: project.labkeyClientApiVersion)`

Gradle will now pull in the locally built version instead. To start using a published
version instead, revert the change to the `settings.gradle` file (and, if necessary, update the `labkeyClientApiVersion`
in the `$LABKEY_ROOT/gradle.properties` file).

When your feature development is done, you should:

- Update the version number in the `build.gradle` file to the next logical SNAPSHOT version corresponding to your changes
following the [SemVer](https://semver.org/) guidelines.  Make sure this is a SNAPSHOT version (e.g., `1.2.3-SNAPSHOT`).
- Update the [change log](CHANGELOG.md) to document what has changed.  You can leave the release date and version alone at this 
point; they will get updated when the next release is published.
- **TBD** Run tests using your SNAPSHOT version of the tests
- Merge your branch into develop if appropriate tests are passing.


### Publishing

Information about the process for publishing new versions of this library
can be found in our [internal documentation](https://internal.labkey.com/Handbook/Dev/wiki-page.view?name=mavenArtifacts)
