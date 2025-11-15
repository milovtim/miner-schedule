#!/usr/bin/env bash

docker build \
  -t miner-schedule \
  --build-arg arch=17-aarch64 .