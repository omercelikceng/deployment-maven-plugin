# deployment-maven-plugin
*autogenerated please use the builder file to change the content*

![Build][Build-shield] 
[![Maintainable][Maintainable-image]][Maintainable-Url]
![Central][Central-shield] 
![Tag][Tag-shield]
![Issues][Issues-shield] 
![Commit][Commit-shield] 
![Size][Size-shield] 
![Dependency][Dependency-shield]
![License][License-shield]
![Label][Label-shield]

[License-Url]: https://www.apache.org/licenses/LICENSE-2.0
[Build-Status-Url]: https://travis-ci.org/YunaBraska/deployment-maven-plugin
[Build-Status-Image]: https://travis-ci.org/YunaBraska/deployment-maven-plugin.svg?branch=master
[Coverage-Url]: https://codecov.io/gh/YunaBraska/deployment-maven-plugin?branch=master
[Coverage-image]: https://img.shields.io/codecov/c/github/YunaBraska/maven-deployment?style=flat-square
[Maintainable-Url]: https://codeclimate.com/github/YunaBraska/maven-deployment/maintainability
[Maintainable-image]: https://img.shields.io/codeclimate/maintainability/YunaBraska/maven-deployment?style=flat-square
[Javadoc-url]: http://javadoc.io/doc/berlin.yuna/deployment-maven-plugin
[Javadoc-image]: http://javadoc.io/badge/berlin.yuna/deployment-maven-plugin.svg
[Gitter-Url]: https://gitter.im/nats-streaming-server-embedded/Lobby
[Gitter-image]: https://img.shields.io/badge/gitter-join%20chat%20%E2%86%92-brightgreen.svg

[Dependency-shield]: https://img.shields.io/librariesio/github/YunaBraska/deployment-maven-plugin?style=flat-square
[Tag-shield]: https://img.shields.io/github/v/tag/YunaBraska/deployment-maven-plugin?style=flat-square
[Central-shield]: https://img.shields.io/maven-central/v/berlin.yuna/deployment-maven-plugin?style=flat-square
[Size-shield]: https://img.shields.io/github/repo-size/YunaBraska/deployment-maven-plugin?style=flat-square
[Issues-shield]: https://img.shields.io/github/issues/YunaBraska/deployment-maven-plugin?style=flat-square
[License-shield]: https://img.shields.io/github/license/YunaBraska/deployment-maven-plugin?style=flat-square
[Commit-shield]: https://img.shields.io/github/last-commit/YunaBraska/deployment-maven-plugin?style=flat-square
[Label-shield]: https://img.shields.io/badge/Yuna-QueenInside-blueviolet?style=flat-square
[Build-shield]: https://img.shields.io/travis/YunaBraska/deployment-maven-plugin/master?style=flat-square

### Index
* [Motivation](#motivation)
* [Usage](#builder_usage_plugin)
* [Building](#building)
* [Semantic and Versioning](#semantic-and-versioning)
* [Tagging and Committing](#tagging-and-committing)
* [Update dependencies and plugins](#update-dependencies-and-plugins)
* [Settings with Servers and Credentials](#settings-with-servers-and-credentials)
* [Deployment](#deployment)
* [Builder files (like README.builder.md)](#builder-files-like-readmebuildermd)
* [Misc](#misc)
* [TODO](#todo)

### Motivation
Once upon a time i had to define the deployment in each of my applications.
The pom.xml's and bash scripts didn't stop growing with build instructions which my app doesn't care about.
I needed thousands commits for hacky testing of my CI/CD systems even if i just want to do defaults like tagging
or semantic versioning.
So i started this project to keep the build instructions in my environment and have the plugin already tested.
Now i can run with auto configuration my deployments daily.
The plugin will even take care of updating all dependencies as semantic versioning.
And all lived happily ever after.

This plugin will autoconfigure "every" default for you.
Pom file descriptions are not need anymore.
For example:
* Auto configuration
* semantic versioning
* update dependencies and plugins
* maven plugins,
* Readme.md variables and placeholder,
* Tagging,
* [...]
and much more while you can still use the original maven userProperties and/or systemProperties (ignoring ".",
"_", "-") to configure the plugins

### Usage as plugin
*version = \<version>java.major/release.minor/features/fixes\</version>*
````xml
<plugin>
    <groupId>berlin.yuna</groupId>
    <artifactId>deployment-maven-plugin</artifactId>
    <version>12.0.3</version>
</plugin>
````

### Usage as command line
````bash
mvn deployment:run -Djava.doc=true -Djava.source -Dupdate.minor
````
* Will create java doc, java sources, and updates dependencies

### Semantic and Versioning
### Parameters
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| project.version     | String  | ''                 | Sets project version in pom                                                |
| project.snapshot    | Boolean | false              | Adds -SNAPSHOT to project version in pom                                   |
| remove.snapshot     | Boolean | false              | Removes snapshot from version                                              |
| semantic.format     | String  | ''                 | Updates semantic version from regex pattern (overwrites project.version)   |

### Semantic version
* Sets the version coming from branch name (uses git refLog for that)
* The matching branch names has to be defined by using regex
* Syntax:
* ````"<separator>::<major>::<minor>::<patch>"```` 
* Example:
* ````semantic.format="[.-]::release.*::feature.*::bugfix\|hotfix::custom_1.*[A-Z]"````

### Tagging and Committing
### Parameters
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| tag                 | Boolean | false              | Tags the project (with project.version or semantic) if not already exists  |
| tag                 | String  | ${project.version} | Tags the project (with project.version or semantic) if not already exists  |
| tag.break           | Boolean | false              | Tags the project (with project.version or semantic) fails if already exists|
| message             | String  | ${auto}            | Commit msg for tag default = \[project.version] \[branchname], \[tag] ...  |
| scm.provider        | String  | scm:git            | needed for tagging & committing                                            |
| COMMIT              | String  | ''                 | Custom commit message on changes - "false" = deactivate commits            |
* Example tag with project.version (or semantic version if active)
* ````tag```` 
* ````tag=true```` 
* ````tag="my.own.version"```` 
* ````message="new release"```` 
* 'tag.break' parameter will stop tagging if the tag already exists

### Update dependencies and plugins
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| update.minor        | Boolean | false              | Updates parent, properties, dependencies                                   |
| update.major        | Boolean | false              | Updates parent, properties, dependencies                                   |
| update.plugins      | Boolean | false              | Updates plugins                                                            |

# Testing
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| test.run            | Boolean | false              | runs test.unit and test.integration                                        |
| test.unit           | Boolean | false              | runs failsafe for unitTest                                                 |
| test.int            | Boolean | false              | alias for test.integration                                                 |
| test.integration    | Boolean | false              | runs surefire integration, component, contract, smoke                      |
| JACOCO              | Boolean | false              | runs failsafe integration test and surefire unitTest                       |

### Settings with Servers and Credentials
Adding servers additional to the settings.xml.
Its also possible to set the properties as environment variables (same as with every property)
### Parameters
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| Server              | String | ''                  | server id                                                                  |
| Username            | String | ''                  | username                                                                   |
| Password            | String | ''                  | password                                                                   |
| PrivateKey          | String | ''                  | e.g. ${user.home}/.ssh/id_dsa)                                             |
| Passphrase          | String | ''                  | privateKey, passphrase                                                     |
| FilePermissions     | String | ''                  | permissions, e.g. 664, or 775                                              |
| DirectoryPermissions| String | ''                  | permissions, e.g. 664, or 775                                              |
* There are three different ways to configure the maven settings 
* Settings format one
```bash
settings.xml='--ServerId=servername1 --Username=username1 --Password=password --ServerId="servername2" --Username=username2'
```
* Settings format two
```bash
server='serverId1::username1::password1::privateKey1::passphrase1'
server0='serverI2::username2::password2::privateKey2::passphrase2'
server1='serverI3::username3::password3::privateKey3::passphrase3'
server2='serverI4::username4::password4::privateKey4::passphrase4'
[...]
```
* Settings format three
```bash
server1.Id='serverId1'
server1.username='username1'
server1.password='password1'
server1.privateKey='privateKey1'
server1.passphrase='passphrase1'
server2.Id='serverId2'
server2.username='username2'
server2.password='password2'
server2.privateKey='privateKey2'
server2.passphrase='passphrase2'
[...]
```

### Deployment
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| deploy              | Boolean | ''                 | Start deployment                                                           |
| deploy.snapshot     | Boolean | ''                 | Start snapshot deployment && adds temporary "-SNAPSHOT" to the project version |
| deploy.id           | String  | ${settings.get(0)} | Id from server settings or settings.xml - default first setting server id containing ids like 'nexus', 'artifact', 'archiva', 'repository', 'snapshot'|
| deploy.url          | String  | ''                 | url to artifact repository - re-prioritize default setting server id if contains keywords from 'deploy.id' |

### Builder files (like README.builder.md)
### Parameters
| Parameter           | Type    | Default            |  Description                                                                    |
|:--------------------|:--------|:-------------------|:--------------------------------------------------------------------------------|
| builder             | Boolean | false              | Will start translating builder files with pattern "fileName.builder.extension"  |

### Builder file content
* Builder files are **simple templates** mainly used for readme files
* The placeholders will be replaced by **maven environment variables** and **git config variables** (git config -l )
* Git config variables starts with 'git.' like **'git.remote.origin.url'**
* 'target' (optional special variable) defines the target directory in the project
* Example
````text
[var myVariableName]: # (This is my variable value)
[var project.description]: # (This overwrites the maven environment variable)
[var varInVar]: # (This contains !{myVariableName} variable)
[var target]: # (/new/readme/directory/path)
[var target]: # (subDirOfCurrent)
[include]: # (/path/include.file)

# My project name: !{project.name}
## My project git origin url: !{git.remote.origin.url}
### My display own variable: !{varInVar}
````

### Building
#### UNDER CONSTRUCTION (NOT STABLE)
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| clean               | Boolean | false              | cleans target and resolves dependencies                                    |
| clean.cache         | Boolean | false              | Purges local maven repository cache                                        |
| java.doc            | Boolean | false              | Creates java doc (javadoc.jar) if its not a pom artifact                   |
| java.source         | Boolean | false              | Creates java sources (sources.jar) if its not a pom artifact               |
| gpg.pass            | String  | ''                 | Signs artifacts (.asc) with GPG 2.1                                        |

### Misc
#### UNDER CONSTRUCTION (NOT STABLE)
| Parameter           | Type    | Default            |  Description                                                               |
|:--------------------|:--------|:-------------------|:---------------------------------------------------------------------------|
| REPORT              | Boolean | false              | Generates report about version updates                                     |
| test.skip           | Boolean | false              | same as "maven.test.skip"                                                  |
| project.encoding    | Boolean | false              | sets default encoding to every encoding parameter definition               |
| java.version        | Boolean | false              | sets default java version to every java version parameter definition       |
| properties.print    | Boolean/String | ''          | writes all properties to (given fileValue or "all.properties") file        |
| changes.push        | String  | ''                 | push changes to specific branch                                            |

### Requirements
* \[JAVA\] for maven 
* \[MAVEN\] to run maven commands
* \[GIT\] for tagging

### Technical links
* [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)
* [maven-source-plugin](https://maven.apache.org/plugins/maven-source-plugin/)
* [maven-surefire-plugin](http://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html)
* [versions-maven-plugin](https://www.mojohaus.org/versions-maven-plugin/set-mojo.html)
* [maven-gpg-plugin](http://maven.apache.org/plugins/maven-gpg-plugin/usage.html)
* [maven-scm-plugin](http://maven.apache.org/scm/maven-scm-plugin/plugin-info.html)
* [upload-an-artifact-into-Nexus](https://support.sonatype.com/hc/en-us/articles/213465818-How-can-I-programmatically-upload-an-artifact-into-Nexus-2-)

### TODO
* [ ] finish converting from bash to real mojo
* [ ] Jacoco
* [ ] plugin updater exclusions
* [ ] plugin updater remove inner bash command
* [ ] update testProject too
* [ ] git last commit as variables
* [ ] Git credentials
* [ ] Git push when changes are made
* [ ] DuplicateFinder
* [ ] Custom run script after each task
* [ ] Shortcuts "live circle"/"workflow"
* [ ] provide configurations json file in target/environment.json file before running anything for additional usage
* [ ] Report
* [ ] test semantic versioning with characters like 'beta' 
* [ ] tag message can contain environment properties
* [ ] set last commit information to environment
* [ ] not tag when last commit was tag commit
* [ ] set always autoReleaseAfterClose=false and add "mvn nexus-staging:release" to release process
* [ ] Deploy dynamic to nexus
* [ ] Deploy dynamic to artifactory
* [ ] try to use JGit for git service
* [ ] org.sonatype.plugins
* [ ] own or buy logo https://www.designevo.com/apps/logo/?name=blue-hexagon-and-3d-container
![deployment-maven-plugin](src/main/resources/banner.png "deployment-maven-plugin")