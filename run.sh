#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
echo $DIR
java -jar $DIR/HustleCastleDiscordBot.jar -c $DIR/settings.json
