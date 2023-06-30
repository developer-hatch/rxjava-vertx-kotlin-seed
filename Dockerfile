FROM gradle:8.1.1-alpine AS TEMP_BUILD_IMAGE

ARG APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

COPY . .
RUN gradle clean build --stacktrace

FROM azul/zulu-openjdk-alpine:17-latest

ARG ARTIFACT_NAME=zlack-all.jar
ARG VERTX_CONF=vertx-application.conf

ARG APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME $APP_HOME/$VERTX_CONF ./

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Dvertx.cacheDirBase=/tmp", "-jar", "zlack-all.jar", "-Dconfig.file=vertx-application.conf"]
