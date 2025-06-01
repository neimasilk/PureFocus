@echo off
echo PureFocus - Uninstall from Device
echo =================================

REM Set Android SDK path
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

REM Check if Android SDK exists
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    echo Please install Android Studio first.
    pause
    exit /b 1
)

echo Checking device connection...
echo Available devices:
"%ANDROID_HOME%\platform-tools\adb.exe" devices

REM Check if any device is connected
for /f "skip=1 tokens=1" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" devices') do (
    if not "%%i"=="" (
        echo ✅ Device detected: %%i
        goto :uninstall_app
    )
)

echo ❌ ERROR: No device detected or ADB not working
echo.
echo Troubleshooting:
echo 1. Make sure your device is connected via USB
echo 2. Enable USB Debugging in Developer Options
echo 3. Authorize the computer on your device
echo 4. Try: adb kill-server && adb start-server
pause
exit /b 1

:uninstall_app
echo.
echo Checking if PureFocus is installed...
"%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr purefocus

if %ERRORLEVEL% neq 0 (
    echo ❌ PureFocus is not installed on this device.
    echo.
    echo Current installed packages containing 'pure':
    "%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr -i pure
    if %ERRORLEVEL% neq 0 (
        echo No packages found containing 'pure'
    )
    pause
    exit /b 0
)

echo ✅ Found PureFocus installation.
echo.
echo Getting app information...
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell dumpsys package com.neimasilk.purefocus ^| findstr "versionName" 2^>nul') do echo App Version: %%i

echo.
echo Force stopping app before uninstall...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am force-stop com.neimasilk.purefocus

echo.
echo Uninstalling PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" uninstall com.neimasilk.purefocus

if %ERRORLEVEL% equ 0 (
    echo ✅ PureFocus uninstalled successfully!
    echo.
    echo Verifying uninstallation...
    "%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr purefocus
    if %ERRORLEVEL% neq 0 (
        echo ✅ Verification complete - PureFocus is completely removed
    ) else (
        echo ⚠️  Warning: Some components may still be present
    )
else
    echo ❌ Failed to uninstall PureFocus
    echo.
    echo Debugging uninstall failure:
    echo 1. App might be a system app (unlikely for PureFocus)
    echo 2. Device might have insufficient permissions
    echo 3. App might be currently running (we tried to stop it)
    echo.
    echo Manual uninstall options:
    echo 1. Go to Settings > Apps > PureFocus > Uninstall
    echo 2. Try: adb shell pm uninstall --user 0 com.neimasilk.purefocus
    echo 3. Clear app data first: adb shell pm clear com.neimasilk.purefocus
)

echo.
echo Useful commands after uninstall:
echo - Reinstall app: run_app_to_device.bat
echo - Check remaining packages: adb shell pm list packages | findstr pure
echo - Clear any remaining data: adb shell pm clear com.neimasilk.purefocus
echo.
pause