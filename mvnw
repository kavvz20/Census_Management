#!/bin/sh
PRG="$0"
DIR=$(dirname "$PRG")
if [ -f "$DIR/.tools/apache-maven-3.9.9/bin/mvn" ]; then
    exec "$DIR/.tools/apache-maven-3.9.9/bin/mvn" "$@"
else
    exec mvn "$@"
fi
