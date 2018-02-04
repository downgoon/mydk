# mydk

``mydk`` is short for ``MY Development Kit``.

## Quick Start

### requirement

- JDK8
- no other dependencies

### import it in your project

``` xml
<dependency>
  <groupId>xyz.downgoon</groupId>
  <artifactId>mydk</artifactId>
  <version>${version}</version>
</dependency>
```

the lastest release version can be found here [g:"xyz.downgoon" a:"mydk"](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22xyz.downgoon%22%20a%3A%22mydk%22)

----

## Tools Listing

### application framework

- [UtinyFramework.java](src/test/java/xyz/downgoon/mydk/framework/utiny/UtinyFrameworkTest.java): similar to 'servlet', 'struts2', 'spring', 'vertx'

- [UtinyFrameworkExample.java](src/test/java/xyz/downgoon/mydk/example/UtinyFrameworkExample.java)

### concurrent

- [ConcurrentResourceContainer](src/test/java/xyz/downgoon/mydk/concurrent/ConcurrentResourceContainerTest.java)
- [TrafficLight.java](src/test/java/xyz/downgoon/mydk/concurrent/TrafficLightTest.java)
- [ConditionTrafficLight.java](src/test/java/xyz/downgoon/mydk/concurrent/ConditionTrafficLightTest.java)
- [Watchdog](src/test/java/xyz/downgoon/mydk/concurrent/WatchdogDemo.java)
- [ThreadContext](src/test/java/xyz/downgoon/mydk/concurrent/ThreadContextTest.java)
- [TagThreadFactory](src/test/java/xyz/downgoon/mydk/concurrent/TagThreadFactoryTest.java)

### sub process calling

- [ProcessFork.java](src/test/java/xyz/downgoon/mydk/process/ProcessForkTest.java)

### testing

- [MiniHttpd.java](src/main/java/xyz/downgoon/mydk/testing/MiniHttpd.java)
- [MiniHttpc.java](src/main/java/xyz/downgoon/mydk/testing/MiniHttpc.java)
- [ConsoleCmder.java](src/main/java/xyz/downgoon/mydk/testing/ConsoleCmder.java)


### util

- [AntPathMatcher.java](src/test/java/xyz/downgoon/mydk/util/AntPathMatcherTest.java)
- [RetryTemplate.java](src/test/java/xyz/downgoon/mydk/util/RetryTemplateTest.java)
- [OsUtils.java](src/main/java/xyz/downgoon/mydk/util/OsUtils.java)
- [FileUtils.java](src/test/java/xyz/downgoon/mydk/util/FileUtilsTest.java)




---

## for Developers

- how to build

[fork me](https://github.com/downgoon/mydk#fork-destination-box) firstly, then

``` bash
git clone https://$YOUR_GITHUB_NAME@github.com/$YOUR_GITHUB_NAME/mydk.git
git remote add upstream https://github.com/downgoon/mydk.git
mvn clean package
```

- how to deploy

``` bash
mvn clean package deploy -Possrh
```
