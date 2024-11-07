FROM openjdk:17

COPY backend.jar backend.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/backend.jar"]