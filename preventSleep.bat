@echo off
title Cloud Keep-Alive Pinger
color 0A
setlocal enabledelayedexpansion

echo ===============================================================================
echo                RENDER / CLOUD PREVENT-SLEEP KEEP-ALIVE PINGER
echo ===============================================================================
echo.

set "TARGET_URL=%1"
if "%TARGET_URL%"=="" (
    set /p "TARGET_URL=Enter your Render App URL (e.g. https://your-app.onrender.com): "
)

if "%TARGET_URL%"=="" (
    echo [ERROR] No URL provided.
    pause
    exit /b 1
)

REM Strip trailing slash if present
if "%TARGET_URL:~-1%"=="/" set "TARGET_URL=%TARGET_URL:~0,-1%"

set "PING_URL=%TARGET_URL%/api/ping"

echo.
echo [OK] Pinging %PING_URL% every 45 seconds...
echo Keep this window open during your presentation or viva.
echo Press Ctrl + C to stop.
echo.

:LOOP
powershell -NoProfile -Command "try { $res = Invoke-WebRequest -Uri '%PING_URL%' -UseBasicParsing -TimeoutSec 10; Write-Host ('[' + (Get-Date).ToString('HH:mm:ss') + '] [OK] Ping successful! Status: ' + $res.StatusCode) -ForegroundColor Green } catch { Write-Host ('[' + (Get-Date).ToString('HH:mm:ss') + '] [WARN] Ping failed or server waking up: ' + $_.Exception.Message) -ForegroundColor Yellow }"
timeout /t 45 /nobreak >nul
goto LOOP
