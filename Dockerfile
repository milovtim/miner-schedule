FROM bellsoft/liberica-openjdk-alpine:17.0.8.1

COPY ./target/miner-schedule-1.0-SNAPSHOT.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "app.jar"]