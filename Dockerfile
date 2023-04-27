FROM amazoncorretto:17.0.7-alpine3.17
WORKDIR /
ADD card-applications-0.0.1-SNAPSHOT.jar card-applications-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD java - jar card-applications-0.0.1-SNAPSHOT.jar