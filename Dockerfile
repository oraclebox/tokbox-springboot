FROM anapsix/alpine-java:8

RUN mkdir /application
RUN mkdir -p /application/logs
COPY build/libs/tokbox-spring-0.0.1-SNAPSHOT.jar /application/tokbox-spring-0.0.1-SNAPSHOT.jar
COPY application-prod.yml /application/application.yml
WORKDIR /application

ENTRYPOINT ["java","-jar","tokbox-spring-0.0.1-SNAPSHOT.jar"]
CMD ["java","-jar","tokbox-spring-0.0.1-SNAPSHOT.jar]
EXPOSE 9000
