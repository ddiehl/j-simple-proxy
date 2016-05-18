#!/bin/sh

echo  "Starting __APP_NAME__"
nohup ./__JAR_NAME__ >> ./logs/init.log 2>&1 &
