#!/bin/bash


PROJECT_PATH="/home/ubuntu/adg-services-v2"
SCRIPT_PATH="./scripts/start-application/dev/customized-run/build-run.sh"

cd $PROJECT_PATH

echo "Run adg-api service"

sh $SCRIPT_PATH api dev