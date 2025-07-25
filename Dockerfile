FROM bellsoft/liberica-openjdk-alpine:17.0.8.1

WORKDIR /app

COPY ./target/miner-schedule-1.0-SNAPSHOT.jar /app/app.jar

COPY ./miner-schedule-db /app/miner-db

ENTRYPOINT ["java", "-jar", "app.jar"]