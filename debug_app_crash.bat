@echo off
echo PureFocus - Debug App Crashes
echo ==============================

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
"%ANDROID_HOME%\platform-tools\adb.exe" devices

REM Check if any device is connected
for /f "skip=1 tokens=1" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" devices') do (
    if not "%%i"=="" (
        goto :debug_start
    )
)

echo ERROR: No device detected!
echo Please connect your device and enable USB debugging.
pause
exit /b 1

:debug_start
echo.
echo ========================================
echo Starting Crash Debugging Session
echo ========================================
echo.

echo [1/7] Clearing previous logs...
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -c
echo ✅ Logs cleared

echo.
echo [2/7] Checking if PureFocus is installed...
"%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr purefocus

if %ERRORLEVEL% neq 0 (
    echo ❌ PureFocus is not installed!
    echo Please run run_app_to_device.bat first to install the app.
    pause
    exit /b 1
)

echo ✅ PureFocus is installed

echo.
echo [3/7] Getting app information...
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell dumpsys package com.neimasilk.purefocus ^| findstr "versionName" 2^>nul') do echo App Version: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.release 2^>nul') do echo Android Version: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.model 2^>nul') do echo Device Model: %%i

echo.
echo [4/7] Checking app permissions...
echo App Permissions:
"%ANDROID_HOME%\platform-tools\adb.exe" shell dumpsys package com.neimasilk.purefocus | findstr "permission"

echo.
echo [5/7] Force stopping app to ensure clean start...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am force-stop com.neimasilk.purefocus
echo ✅ App force stopped

echo.
echo [6/7] Starting app with crash monitoring...
echo Launching PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am start -n com.neimasilk.purefocus/.MainActivity

if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to launch app!
    echo This indicates a serious issue with the app package.
    goto :analyze_logs
)

echo ✅ App launch command sent
echo.
echo [7/7] Monitoring for crashes...
echo ========================================
echo LIVE CRASH MONITORING (Press Ctrl+C to stop)
echo ========================================
echo.
echo Watching for crashes and errors...
echo If the app crashes, details will appear below:
echo.

REM Monitor logs for crashes and errors
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -s AndroidRuntime:E PureFocus:* System.err:* DEBUG:* *:F

echo.
echo Monitoring stopped.

:analyze_logs
echo.
echo ========================================
echo CRASH ANALYSIS
echo ========================================
echo.

echo Generating crash report...
echo.

echo --- RECENT CRASH LOGS ---
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d -s AndroidRuntime:E | findstr -i "purefocus\|fatal\|exception"

echo.
echo --- APP SPECIFIC LOGS ---
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d | findstr -i "purefocus"

echo.
echo --- SYSTEM ERRORS ---
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d -s System.err:*

echo.
echo ========================================
echo TROUBLESHOOTING SUGGESTIONS
echo ========================================
echo.
echo Common crash causes and solutions:
echo.
echo 1. MEMORY ISSUES:
echo    - Close other apps to free memory
echo    - Restart device if low on RAM
echo.
echo 2. PERMISSION ISSUES:
echo    - Check if app has required permissions
echo    - Grant permissions manually in Settings
echo.
echo 3. COMPATIBILITY ISSUES:
echo    - Verify Android version compatibility
echo    - Check if device meets minimum requirements
echo.
echo 4. CORRUPTED INSTALLATION:
echo    - Uninstall and reinstall the app
echo    - Clear app data: adb shell pm clear com.neimasilk.purefocus
echo.
echo 5. DEVELOPMENT ISSUES:
echo    - Check for null pointer exceptions in logs
echo    - Verify all resources are properly included
echo    - Check for missing dependencies
echo.
echo ========================================
echo DETAILED DEBUGGING COMMANDS
echo ========================================
echo.
echo Save full logcat to file:
echo adb logcat -d ^> purefocus_crash_log.txt
echo.
echo Check app memory usage:
echo adb shell dumpsys meminfo com.neimasilk.purefocus
echo.
echo Check app CPU usage:
echo adb shell top -n 1 ^| findstr purefocus
echo.
echo Clear app data (if corrupted):
echo adb shell pm clear com.neimasilk.purefocus
echo.
echo Reinstall app:
echo adb uninstall com.neimasilk.purefocus
echo adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.
echo Check device storage:
echo adb shell df
echo.
echo Monitor real-time performance:
echo adb shell top
echo.
pause