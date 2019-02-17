@ECHO OFF

git submodule init
git submodule update

./gradlew.bar jar
java -jar ./build/libs/SecurityAnalystDashboard
