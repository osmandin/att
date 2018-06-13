FROM maven:3.5.3-jdk-8-alpine

RUN apk update
# RUN apk add nano

VOLUME /src
WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml

# do this so that maven doesnt download the world every time
RUN mvn verify clean --fail-never

# Adding source, compile and package into a fat jar

ADD src /code/src
RUN ["mvn", "clean", "package", "-Pdev"]

EXPOSE 8080
CMD ["java", "-jar", "target/att-0.0.1-SNAPSHOT.war"]
