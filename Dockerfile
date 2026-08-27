FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system quarkus && useradd --system --gid quarkus --create-home --home-dir /home/quarkus quarkus

COPY --from=build /workspace/target/quarkus-app/lib/ /app/lib/
COPY --from=build /workspace/target/quarkus-app/app/ /app/app/
COPY --from=build /workspace/target/quarkus-app/quarkus/ /app/quarkus/
COPY --from=build /workspace/target/quarkus-app/quarkus-run.jar /app/

RUN chown -R quarkus:quarkus /app

USER quarkus

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+UseG1GC -Duser.timezone=UTC"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
  CMD wget -q -O- http://127.0.0.1:8080/health || exit 1

CMD ["java", "-jar", "/app/quarkus-run.jar"]
