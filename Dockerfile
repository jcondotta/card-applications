FROM anapsix/alpine-java
ADD target/card-applications-*.jar /home/myjar.jar
CMD ["java","-jar","/home/myjar.jar"]