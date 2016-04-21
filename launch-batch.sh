#!/bin/bash

rm cwebapp/target/cwebapp-1.0-SNAPSHOT.jar

./launch.sh 8001 1701 1702
./launch.sh 8002 1801 1802

./bootstrap.sh localhost:8001 localhost:8002
./bootstrap.sh localhost:8002 localhost:8001

./clean.sh
rm configs/8001/state.json
rm configs/8002/state.json
