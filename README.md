[fabrician.org](http://fabrician.org/)
==========================================================================
Distribution Maven Plugin
==========================================================================

Maven Plugin for creating distributions and uploading grid libraries to SilverFabric.

Release builds are available from [Maven Central](http://search.maven.org/#artifactdetails|org.fabrician.maven-plugins|distribution-plugin|1.2|maven-plugin).  The latest release is 1.2.

## Distribution Packaging

`distribution:package`

#### Full name:

`org.fabrician.maven-plugins:distribution-plugin:1.1:package`

#### Description:

Packages a grid libary given the grid-library.xml resource directory and 3rd party directory or zip/tar.gz.

#### Parameters:

<table>
  <tr>
    <th>Name</th><th>Description</th><th>Default Value</th><th>Version Added</th>
  </tr>
  <tr>
    <td>distroFilename</td><td>The target name of the distribution grid library.  The extension must be tar.gz or zip.</td><td></td><td>1.0</td>
  </tr>
  <tr>
    <td>distroSource</td><td>The URL, file or directory that contains the software to bundle.  When specifying an URL, connectionTimeout and readTimeout can be specified for the file download.</td><td></td><td>1.0</td>
  </tr>
  <tr>
    <td>includes</td><td>A set of file patterns to include from the distroSource.</td><td></td><td>1.2-SNAPSHOT</td>
  </tr>
  <tr>
    <td>excludes</td><td>A set of file patterns to exclude from the distroSource.</td><td></td><td>1.2-SNAPSHOT</td>
  </tr>
  <tr>
    <td>distroResources</td><td>The directory that contains the grid library resources such as a grid-library.xml.</td><td>src/main/resources/distribution</td><td>1.0</td>
  </tr>
  <tr>
    <td>distroAlternateRootDirectory</td><td>The alternate root directory name in the resulting grid library.  Useful when the software zip or tar.gz basedir changes across versions.  For example, a.zip:foo/bar/x.html -> b.tar.gz:myAltDir/bar/x.html when distroAlternateRootDirectory is set to myAltDir.</td><td></td><td>1.0</td>
  </tr>
</table>

#### Example:

```xml
<plugin>
    <groupId>org.fabrician.maven-plugins</groupId>
    <artifactId>distribution-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <distroSource>/tmp/apache-activemq-5.7.0-bin.zip</distroSource>
        <distroFilename>${project.build.directory}/active-mq-5.7.0-distro.tar.gz</distroFilename>
        <distroResources>src/main/resources/distribution</distroResources>
        <distroAlternateRootDirectory>apache-activemq</distroAlternateRootDirectory>
    </configuration>
    <executions>
        <execution>
            <id>package-distribution</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Grid Library Upload

`distribution:upload`

#### Full name:

`org.fabrician.maven-plugins:distribution-plugin:1.0:upload`

#### Description:

Upload one or more grid libraries to a running Silver Fabric Broker.

#### Parameters:

<table>
  <tr>
    <th>Name</th><th>Description</th><th>Default Value</th><th>Version Added</th>
  </tr>
  <tr>
    <td>brokerUrl</td><td>Silver Fabric Broker URL.  For Silver Fabric 5.5, use http://localhost:8000/livecluster/rest/v1/sf/gridlibs/archives.</td><td>http://localhost:8000/livecluster/gridlibs/archives</td><td>1.0</td>
  </tr>
  <tr>
    <td>brokerUsername</td><td>Silver Fabric Broker user name</td><td>admin</td><td>1.0</td>
  </tr>
  <tr>
    <td>brokerPassword</td><td>Silver Fabric Broker password</td><td>admin</td><td>1.0</td>
  </tr>
  <tr>
    <td>serverId</td><td>Which server credentials to use from the user's settings.xml.  This hasn't been implemented.</td><td></td><td></td>
  </tr>
  <tr>
    <td>gridlibOverwrite</td><td>Overwrite the grid libary if it already exists.</td><td>false</td><td>1.0</td>
  </tr>
  <tr>
    <td>includes</td><td>A set of file patterns to include in the upload.</td><td></td><td>1.0</td>
  </tr>
  <tr>
    <td>excludes</td><td>A set of file patterns to exclude from the upload.</td><td></td><td>1.0</td>
  </tr>
</table>

#### Example:

```xml
<plugin>
    <groupId>org.fabrician.maven-plugins</groupId>
    <artifactId>distribution-plugin</artifactId>
    <version>1.1</version>
    <executions>
    <plugin> 
        <execution>
            <id>upload-gridlibs</id>
            <phase>install</phase>
            <goals>
                <goal>upload</goal>
            </goals>
            <configuration>
                 <includes>
                     <include>*.tar.gz</include>
                 </includes>
            </configuration>
        </execution>
    </executions>
</plugin>
```
