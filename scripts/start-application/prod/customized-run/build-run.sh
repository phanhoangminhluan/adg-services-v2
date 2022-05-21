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
LOGBACK_PATH="$SERVER_PROJECT_PATH/src/main/resources/logback-dev.xml"

JAR_PATH="$SOURCE/adg-api-0.0.1-SNAPSHOT.jar"

ACTIVE_PROFILE=$1
MODE=$2

cd $PROJECT_PATH

mvn clean install

echo ""
echo "RUNNING COMMAND: START ------------------------------------------------------------------------------------------------------------"

echo java \
    -Xms256M \
    -Xmx512M \
    -Dspring.profiles.active="$ACTIVE_PROFILE-$MODE" \
    -Dlogging.config="$LOGBACK_PATH" \
    -DACTIVE_PROFILE="$ACTIVE_PROFILE" \
    -DLOG_DIR="$LOG_DIR" \
    -jar "$JAR_PATH" \
    com.adg.server.AdgServerApplication "$ACTIVE_PROFILE"

echo "RUNNING COMMAND: END ------------------------------------------------------------------------------------------------------------"
echo ""

java \
    -Xms256M \
    -Xmx512M \
    -Dspring.profiles.active="$ACTIVE_PROFILE-$MODE" \
    -DACTIVE_PROFILE="$ACTIVE_PROFILE" \
    -DLOG_DIR="$LOG_DIR" \
    -jar "$JAR_PATH" \
    com.adg.server.AdgServerApplication "$ACTIVE_PROFILE"

