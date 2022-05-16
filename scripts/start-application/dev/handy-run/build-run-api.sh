#!/bin/bash


PROJECT_PATH="/Users/luan.phm/engineering/Projects/ADongGroup/adg-services-v2"
SCRIPT_PATH="./scripts/start-application/dev/customized-run/build-run.sh"

cd $PROJECT_PATH

echo "Run adg-api service"

sh $SCRIPT_PATH api dev