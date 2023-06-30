FROM azul/zulu-openjdk-alpine:11

EXPOSE 8080
ADD /build/libs/rxjava-vertx-kotlin-seed-all.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
