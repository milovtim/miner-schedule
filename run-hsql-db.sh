#!/usr/bin/env bash

nohup java -jar target/miner-schedule-1.0-SNAPSHOT.jar \
  --spring.profiles.active=hsql,night &