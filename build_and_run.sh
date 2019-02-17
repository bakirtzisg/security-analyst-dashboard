#!/bin/bash

git submodule init
git submodule update

./gradlew jar

java -jar ./build/libs/SecurityAnalystDashboard.jar
