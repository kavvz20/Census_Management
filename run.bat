@echo off
title Census Management System Launcher
color 0B
setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0"
cd /d "%ROOT_DIR%"

echo ===============================================================================
echo            CENSUS MANAGEMENT SYSTEM - 1-CLICK LAUNCHER
echo ===============================================================================
echo.

REM 1. Auto-detect Java Installation
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-26.0.2" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
    ) else if exist "C:\Program Files\Java\latest" (
        set "JAVA_HOME=C:\Program Files\Java\latest"
    ) else (
        for /d %%D in ("C:\Program Files\Java\jdk-*") do (
            if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D"
        )
        if "!JAVA_HOME!"=="" (
            for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
                if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D"
            )
        )
    )
)

if not "%JAVA_HOME%"=="" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    where java.exe >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "JAVA_EXE=java.exe"
    ) else (
        color 0C
        echo [ERROR] Java JDK 17+ was not detected on this system.
        echo Please ensure Java 17+ is installed or set JAVA_HOME.
        pause
        exit /b 1
    )
)

echo [OK] Using Java: !JAVA_HOME!
echo [OK] Database  : SQLite (Embedded single-file 'census.db' - Zero configuration)
echo.

if "%1"=="--mysql" goto START_MYSQL
if "%1"=="--build" goto REBUILD
if "%1"=="--test"  goto RUN_TESTS

REM Check if target JAR exists, if not build it once
if not exist "target\census-management-system-1.0.0.jar" (
    echo [INFO] First-time setup detected. Packaging executable JAR...
    call mvnw.cmd clean package -DskipTests
    if !ERRORLEVEL! neq 0 (
        color 0C
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
)

echo ===============================================================================
echo  Launching Census Management System...
echo  Your browser will open automatically at: http://localhost:8080/login
echo ===============================================================================
echo.

REM Automatically open browser after 3 seconds
start /b cmd /c "timeout /t 3 /nobreak >nul && start http://localhost:8080/login"

REM Run the application with SQLite
"%JAVA_EXE%" -jar target\census-management-system-1.0.0.jar

goto END

:START_MYSQL
echo Starting with MySQL profile...
start /b cmd /c "timeout /t 3 /nobreak >nul && start http://localhost:8080/login"
"%JAVA_EXE%" -jar target\census-management-system-1.0.0.jar --spring.profiles.active=mysql
goto END

:REBUILD
echo Rebuilding project...
call mvnw.cmd clean package -DskipTests
goto END

:RUN_TESTS
echo Running tests...
call mvnw.cmd test
goto END

:END
pause
