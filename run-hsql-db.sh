#!/usr/bin/env bash

nohup java -jar target/miner-schedule-1.0-SNAPSHOT.jar \
  --spring.profiles.active=hsql,night &


# docker run --detach --name selenium-chromium \
# -p 4444:4444 -p 5900:5900 -p 7900:7900 \
# --shm-size 2g selenium/standalone-chromium:latest
