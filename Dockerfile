ARG arch=17
FROM bellsoft/liberica-openjdk-alpine:$arch

WORKDIR /app

COPY ./target/miner-schedule-1.0-SNAPSHOT.jar /app/app.jar

COPY ./miner-schedule-db/ /app/miner-schedule-db/

ENTRYPOINT ["java", "-jar", "app.jar"]