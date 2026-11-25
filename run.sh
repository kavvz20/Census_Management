#!/bin/bash
# ----------------------------------------------------------------------------
# Census Management System - 1-Command Launcher (Linux / macOS / Git Bash)
# ----------------------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

echo "==============================================================================="
echo "            CENSUS MANAGEMENT SYSTEM - 1-CLICK LAUNCHER"
echo "==============================================================================="
echo ""

# Auto-detect Java
if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/bin/java" ]; then
        JAVA_CMD="java"
    elif [ -x "$JAVA_HOME/bin/java" ]; then
        JAVA_CMD="$JAVA_HOME/bin/java"
    else
        JAVA_CMD="java"
    fi
else
    JAVA_CMD="$JAVA_HOME/bin/java"
fi

PROFILE="default"
if [ "$1" == "--mysql" ]; then
    PROFILE="mysql"
    echo "Starting with MySQL Profile..."
else
    echo "Starting in Zero-Config H2 Mode (Instant)..."
fi

# Open browser in background after 3 seconds
(sleep 3 && {
    if which xdg-open > /dev/null; then
        xdg-open "http://localhost:8080"
    elif which open > /dev/null; then
        open "http://localhost:8080"
    fi
}) &

if [ -f "target/census-management-system-1.0.0.jar" ]; then
    "$JAVA_CMD" -jar target/census-management-system-1.0.0.jar --spring.profiles.active="$PROFILE"
else
    ./mvnw spring-boot:run -Dspring-boot.run.profiles="$PROFILE"
fi
