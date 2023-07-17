FROM gradle:8.1.1-alpine

RUN gradle --version && java -version

WORKDIR .
COPY build.gradle settings.gradle ./

RUN gradle clean build --stacktrace > /dev/null 2>&1 || true

COPY . .

RUN gradle clean build --stacktrace

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Dvertx.cacheDirBase=/tmp", "-jar", "build/libs/rxjava-vertx-kotlin-seed-all.jar", "-Dconfig.file=vertx-application-local.conf"]
