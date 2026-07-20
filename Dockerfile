FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /work

COPY --from=build /workspace/target/quarkus-app/lib/ /work/lib/
COPY --from=build /workspace/target/quarkus-app/app/ /work/app/
COPY --from=build /workspace/target/quarkus-app/quarkus/ /work/quarkus/
COPY --from=build /workspace/target/quarkus-app/quarkus-run.jar /work/

ENV JAVA_OPTS=""
EXPOSE 8080

CMD ["java", "-jar", "/work/quarkus-run.jar"]
