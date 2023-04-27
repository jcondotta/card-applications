FROM amazoncorretto:17-alpine-jdk

EXPOSE 8080

WORKDIR /applications

COPY target/card-applications-0.0.1-SNAPSHOT.jar /applications/sample-application.jar

ENTRYPOINT ["java","-jar", "sample-application.jar"]