#!/usr/bin/env bash

PID="$(grep -Po '(?<=PID )\d+' nohup.out|tail -1)"
echo "Try to kill PID=$PID"
kill "$PID"