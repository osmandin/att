# Archives Transfer Tool

Build
--------------

The project can be built using Apache Maven, and the resulting .war file can just be dropped into Tomcat 
(or just launched with `java -jar`).

```sh

# from the folder, run the build, and package it:

mvn clean install -P dev

# to test it:

java -jar target/att-0.0.1-SNAPSHOT.war

```

Test
--------

Visit `http://localhost:8080/att`.
