FROM openjdk:8-jdk-alpine

ENV JAR=rxjava-vertx-kotlin-seed.jar

COPY kube-application.conf /opt/app/
COPY build/libs/$JAR /opt/app/app.jar

EXPOSE 8080

WORKDIR /opt/app
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Dvertx.cacheDirBase=/tmp", "-jar", "app.jar", "-Dconfig.file=kube-application.conf"]
