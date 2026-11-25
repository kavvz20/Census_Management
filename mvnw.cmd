@REM ----------------------------------------------------------------------------
@REM Census Management System Maven Wrapper
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

REM 1. Auto-detect JAVA_HOME if not already set
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-26.0.2" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
    ) else if exist "C:\Program Files\Java\latest" (
        set "JAVA_HOME=C:\Program Files\Java\latest"
    ) else (
        for /d %%D in ("C:\Program Files\Java\jdk-*") do (
            if exist "%%D\bin\javac.exe" set "JAVA_HOME=%%D"
        )
        if "!JAVA_HOME!"=="" (
            for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
                if exist "%%D\bin\javac.exe" set "JAVA_HOME=%%D"
            )
        )
    )
)

if not "%JAVA_HOME%"=="" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

REM 2. Invoke Maven binary
set "DIR=%~dp0"
if exist "%DIR%.tools\apache-maven-3.9.9\bin\mvn.cmd" (
    call "%DIR%.tools\apache-maven-3.9.9\bin\mvn.cmd" %*
) else (
    where mvn >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        call mvn %*
    ) else (
        echo Error: Maven executable not found in .tools or PATH.
        exit /b 1
    )
)
