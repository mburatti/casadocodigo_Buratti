FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM tomcat:10.1-jre21-temurin
WORKDIR /usr/local/tomcat/webapps

COPY --from=build /workspace/target/casadocodigo.war /usr/local/tomcat/webapps/ROOT.war

ENV CATALINA_OPTS=""
EXPOSE 8080

CMD ["catalina.sh", "run"]
