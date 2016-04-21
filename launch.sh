#!/bin/bash

if [ ! -f cwebapp/target/cwebapp-1.0-SNAPSHOT-jar-with-dependencies.jar ]; then
	mvn package -DskipTests
fi

java -jar cwebapp/target/cwebapp-1.0-SNAPSHOT-jar-with-dependencies.jar --app-port $1 --data-dir ./configs/$1 --dht-port-1 $2 --dht-port-2 $3
