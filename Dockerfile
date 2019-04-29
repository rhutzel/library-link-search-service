FROM openjdk:12
VOLUME /tmp
COPY *.jar library-link-search-server.jar
ENTRYPOINT ["java","-jar","/library-link-search-server.jar"]
