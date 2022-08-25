## About

The GreenPanda Core is a plugin that a lot of the other plugins in the Green/Panda plugin pack depend on. It has interfaces and utilities to make plugin development easier and faster. 

## Installing

Just drag and drop the jar file into your plugins folder along with any other plugins you want to install.

## API

Feel free to use the GreenPandaCore to make your own plugins! It's licenced under the MIT license, which means you can use it in basically any way that you want.

Until it's available on Maven Central, you'll have to build the project and add it to your local m2 repository manually if you want to use GreenPandaCore as a dependency.

First, make sure you have [maven][1] installed on your computer. Download the project to your computer and open it in your IDE of choice. In the terminal, run `mvn package`. After that's done, run `mvn install:install-file -Dfile="target/core-0.0.2.jar" -DpomFile="pom.xml" -Dsources="target/core-0.0.2-sources.jar"`. The name of the jar file will change depending on the version, so you might have to replace "core-0.0.2.jar" with the correct version for it to work. After that, it should be installed!

Now to actually use the dependency in your project, all you have to do is add it in your pom.xml. Add the following, replacing version with your desired version:
```xml
<dependency>
  <groupId>dev.michaud.greenpanda</groupId>
  <artifactId>core</artifactId>
  <version>0.0.2</version>
</dependency>
```
And now your project is all set up! The commands we ran earlier installed the sources jar, so you can view the source code and comments from your IDE. You can also build the javadocs by running `mvn javadoc:javadoc`, and it will generate it to the target\site folder.

[1]: https://maven.apache.org/install.html
