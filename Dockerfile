FROM openjdk:11
EXPOSE 8080
WORKDIR /Udee
COPY target/Udee-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "Udee-0.0.1-SNAPSHOT.jar" ]