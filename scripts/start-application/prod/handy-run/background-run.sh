
#!/bin/bash

############ HOW TO USE ############
# Run without build
# sh run.sh <profile> <mode>
# Ex: sh run.sh scheduler dev
#
# Before using this script. Please:
# - Specify PROJECT_PATH: path to your project
# - Create folder named 'log' in PROJECT_PATH
####################################


PROJECT_PATH="/home/ubuntu/adg-services-v2"
SERVER_PROJECT_PATH="$PROJECT_PATH/adg-api"

SOURCE="$SERVER_PROJECT_PATH/target"
LOG_DIR="$PROJECT_PATH/log"

JAR_PATH="$SOURCE/adg-api-0.0.1-SNAPSHOT.jar"

ACTIVE_PROFILE="api"
MODE="prod"

cd $PROJECT_PATH

echo ""
echo "RUNNING COMMAND: START ------------------------------------------------------------------------------------------------------------"

echo java \
         -Xms256M \
         -Xmx512M \
         -Dspring.profiles.active="$ACTIVE_PROFILE-$MODE" \
         -DLOG_DIR="$LOG_DIR" \
         -jar "$JAR_PATH" \
         com.adg.server.AdgServerApplication "$ACTIVE_PROFILE"

echo "RUNNING COMMAND: END ------------------------------------------------------------------------------------------------------------"
echo ""

java \
    -Xms256M \
    -Xmx512M \
    -Dspring.profiles.active="$ACTIVE_PROFILE-$MODE" \
    -DLOG_DIR="$LOG_DIR" \
    -jar "$JAR_PATH" \
    com.adg.server.AdgServerApplication "$ACTIVE_PROFILE"

