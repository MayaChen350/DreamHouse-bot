#!/bin/sh

### Check if the java command is available ###

if [ -z "$(command -v java)" ]; then
    echo "Error! Java is not installed or the "Java" command is not available"
    exit 1
fi

### Search for bot jar ###

JAR=$(ls ./build/libs/DreamHouse-bot-*-all.jar | tail -n 1)
if [ -z "$JAR" ]; then
    JAR=$(ls DreamHouse-bot-*-all.jar | tail -n 1)
fi

# TODO: Ask if want to check in each subdirectories if not found

### Execute it or print an error message ###

if [ ! -z "$JAR" ]; then
    java -jar $JAR
else
    echo "Error! A Dreamhouse-bot JAR was not found."
fi

# TODO: Restarting the bot automatically
