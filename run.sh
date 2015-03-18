#!/bin/sh

JAVA_VER=0
JAVA_VER=$(java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')

#echo $JAVA_VER

if [ 18 -gt $JAVA_VER ]
then
    echo "This application requires Java 8"
    echo "You are running:"
    java -version
    exit 1
fi

java -jar mazeSolverGui.jar