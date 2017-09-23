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

### concurrent

- [ConcurrentResourceContainer](src/test/java/xyz/downgoon/mydk/concurrent/ConcurrentResourceContainerTest.java)
- [TrafficLight.java](src/test/java/xyz/downgoon/mydk/concurrent/TrafficLightTest.java)
- [Watchdog](src/test/java/xyz/downgoon/mydk/concurrent/WatchdogDemo.java)

### testing

- [MiniHttpd.java](src/main/java/xyz/downgoon/mydk/testing/MiniHttpd.java)
- [MiniHttpc.java](src/main/java/xyz/downgoon/mydk/testing/MiniHttpc.java)
- [ConsoleCmder.java](src/main/java/xyz/downgoon/mydk/testing/ConsoleCmder.java)
