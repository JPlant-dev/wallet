FROM openjdk:11-jre-slim

# Prerequisites
run apt-get upgrade

WORKDIR /muchbetter

USER daemon

ADD /build/libs/muchbetter-1.0-SNAPSHOT.jar /muchbetter/build/libs/muchbetter-1.0-SNAPSHOT.jar

CMD ["java", "-jar", "/muchbetter/build/libs/muchbetter-1.0-SNAPSHOT.jar", "host.docker.internal"]

EXPOSE 5050