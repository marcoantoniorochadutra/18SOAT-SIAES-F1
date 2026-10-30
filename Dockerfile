# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-26-alpine AS build
WORKDIR /workspace
COPY pom.xml lombok.config ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline
COPY src/main src/main
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -Dmaven.test.skip=true -Djacoco.skip=true \
    && mv target/siaes-*.jar target/app.jar \
    && java -Djarmode=tools -jar target/app.jar extract --layers --destination target/extracted

FROM eclipse-temurin:26-jdk-alpine AS runtime
COPY --from=build /workspace/target/extracted /extracted
RUN modules="$(jdeps --ignore-missing-deps --recursive --multi-release 26 --print-module-deps \
        --class-path '/extracted/dependencies/lib/*:/extracted/snapshot-dependencies/lib/*' \
        /extracted/application/app.jar)" \
    && jlink --add-modules "${modules},jdk.crypto.ec,jdk.localedata,jdk.naming.dns,jdk.zipfs" \
        --include-locales=en,pt-BR \
        --strip-debug --no-man-pages --no-header-files --compress=zip-9 \
        --output /jre

FROM alpine:3.23
ENV JAVA_HOME=/opt/java \
    PATH="/opt/java/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
RUN addgroup -S siaes && adduser -S -G siaes siaes
COPY --from=runtime /jre /opt/java
WORKDIR /app
COPY --from=build /workspace/target/extracted/dependencies/ ./
COPY --from=build /workspace/target/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/target/extracted/application/ ./
USER siaes
EXPOSE 8715
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q --spider "http://127.0.0.1:${SERVER_PORT:-8715}${SERVER_CONTEXT_PATH%/}/v3/api-docs" || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
