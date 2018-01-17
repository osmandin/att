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

Security
----------

The app currently uses in memory authentication for admin users. This
might get replaced with OAuth authentication in future.

The app generates one time links for users, instead of having
to create one time user accounts.

The design is based on:

https://stackoverflow.com/questions/20318592/spring-mvc-how-to-generate-temporary-link


Technical
----------

Read up on project lombok here:

http://www.baeldung.com/intro-to-project-lombok

https://projectlombok.org/features/all

Data Model
-----------

A user can belong to multiple departments.
A department can have multiple users.
A department can have multiple submission agreements.
A submission agreement departments on a department.



