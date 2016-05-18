#!/bin/sh

PID=$(pgrep -f "__JAR_NAME__")
if [ ! -z "$PID" ]
then
  echo "Stopping Process"
  kill $PID
  echo "Process Stopped"
else
  echo "Process not running"
fi
