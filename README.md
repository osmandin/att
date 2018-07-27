# Archives Transfer Tool

Build (with Docker)
-------------------

The project can be launched with Docker

```sh
# first time only:
docker pull maven:3.5.3-jdk-8-alpine

# from the folder, build the docker image (it will take a while the first time):

docker build -t att:1.0 .

# now run it:

docker run -p 8080:8080 att:1.0

# the app should now be live at localhost:8080/att
 
```

Now if you want to make a change to the app:

- Hit ```Ctrl-C```
- Edit /src/main/resources/templates/AddDepartment.html (for example)
- Run the image build command (```docker build```), as described above (it should take a second now).
- Run the image again (```docker run```).

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

Data Model
-----------

- A user can be associated with multiple departments.
- A department can have multiple users.
- A department can have multiple submission agreements.
- A submission agreement is tied to a department.



