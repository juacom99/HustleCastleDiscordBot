#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
echo $DIR
java -jar $DIR/HustleCastleDiscordBot-1.0-SNAPSHOT.jar -c /home/jomartinez/NetBeansProjects/HustleCastleDiscordBot/target/settings.json
