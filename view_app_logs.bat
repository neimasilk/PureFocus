@echo off
echo PureFocus - Live App Log Viewer
echo ===============================

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
        echo ✅ Device detected: %%i
        goto :log_menu
    )
)

echo ❌ ERROR: No device detected!
echo Please connect your device and enable USB debugging.
pause
exit /b 1

:log_menu
echo.
echo ========================================
echo LOG VIEWING OPTIONS
echo ========================================
echo.
echo 1. View ALL app logs (PureFocus only)
echo 2. View ERROR logs only
echo 3. View CRASH logs and stack traces
echo 4. View PERFORMANCE logs (memory, CPU)
echo 5. View SYSTEM logs related to app
echo 6. Save logs to file
echo 7. Clear all logs and start fresh
echo 8. Monitor live logs (real-time)
echo 9. Exit
echo.
set /p choice=Choose an option (1-9): 

if "%choice%"=="1" goto :all_logs
if "%choice%"=="2" goto :error_logs
if "%choice%"=="3" goto :crash_logs
if "%choice%"=="4" goto :performance_logs
if "%choice%"=="5" goto :system_logs
if "%choice%"=="6" goto :save_logs
if "%choice%"=="7" goto :clear_logs
if "%choice%"=="8" goto :live_logs
if "%choice%"=="9" goto :exit

echo Invalid choice. Please try again.
goto :log_menu

:all_logs
echo.
echo ========================================
echo ALL PUREFOCUS LOGS
echo ========================================
echo Press Ctrl+C to stop viewing
echo.
"%ANDROID_HOME%\platform-tools\adb.exe" logcat | findstr -i "purefocus\|com.neimasilk.purefocus"
goto :log_menu

:error_logs
echo.
echo ========================================
echo ERROR LOGS ONLY
echo ========================================
echo Press Ctrl+C to stop viewing
echo.
"%ANDROID_HOME%\platform-tools\adb.exe" logcat *:E | findstr -i "purefocus\|com.neimasilk.purefocus"
goto :log_menu

:crash_logs
echo.
echo ========================================
echo CRASH LOGS AND STACK TRACES
echo ========================================
echo.
echo Recent crashes:
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d -s AndroidRuntime:E | findstr -i "purefocus\|fatal\|exception"
echo.
echo Full crash details:
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d | findstr -A 20 -B 5 -i "fatal.*purefocus\|exception.*purefocus"
echo.
echo Press any key to return to menu...
pause >nul
goto :log_menu

:performance_logs
echo.
echo ========================================
echo PERFORMANCE MONITORING
echo ========================================
echo.
echo Memory usage:
"%ANDROID_HOME%\platform-tools\adb.exe" shell dumpsys meminfo com.neimasilk.purefocus
echo.
echo CPU usage:
"%ANDROID_HOME%\platform-tools\adb.exe" shell top -n 1 | findstr purefocus
echo.
echo Battery usage:
"%ANDROID_HOME%\platform-tools\adb.exe" shell dumpsys batterystats | findstr -i purefocus
echo.
echo Performance logs:
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d | findstr -i "performance\|memory\|cpu\|battery" | findstr -i purefocus
echo.
echo Press any key to return to menu...
pause >nul
goto :log_menu

:system_logs
echo.
echo ========================================
echo SYSTEM LOGS RELATED TO APP
echo ========================================
echo Press Ctrl+C to stop viewing
echo.
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -s ActivityManager:* PackageManager:* WindowManager:* | findstr -i purefocus
goto :log_menu

:save_logs
echo.
echo ========================================
echo SAVE LOGS TO FILE
echo ========================================
echo.
set timestamp=%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set timestamp=%timestamp: =0%
set logfile=purefocus_logs_%timestamp%.txt

echo Saving all logs to: %logfile%
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d > "%logfile%"

if exist "%logfile%" (
    echo ✅ Logs saved successfully to: %logfile%
    echo File size: 
    for %%A in ("%logfile%") do echo %%~zA bytes
else
    echo ❌ Failed to save logs
)

echo.
echo Also saving PureFocus-specific logs...
set applogfile=purefocus_app_logs_%timestamp%.txt
"%ANDROID_HOME%\platform-tools\adb.exe" logcat -d | findstr -i "purefocus\|com.neimasilk.purefocus" > "%applogfile%"

if exist "%applogfile%" (
    echo ✅ App-specific logs saved to: %applogfile%
else
    echo ❌ Failed to save app-specific logs
)

echo.
echo Press any key to return to menu...
pause >nul
goto :log_menu

:clear_logs
echo.
echo ========================================
echo CLEAR ALL LOGS
echo ========================================
echo.
echo This will clear all existing logs on the device.
set /p confirm=Are you sure? (y/n): 

if /i "%confirm%"=="y" (
    "%ANDROID_HOME%\platform-tools\adb.exe" logcat -c
    echo ✅ All logs cleared successfully!
else
    echo Operation cancelled.
)

echo.
echo Press any key to return to menu...
pause >nul
goto :log_menu

:live_logs
echo.
echo ========================================
echo LIVE LOG MONITORING
echo ========================================
echo.
echo Monitoring live logs for PureFocus...
echo Press Ctrl+C to stop monitoring
echo.
echo Starting live log stream...
echo ----------------------------------------
"%ANDROID_HOME%\platform-tools\adb.exe" logcat | findstr -i "purefocus\|com.neimasilk.purefocus"
goto :log_menu

:exit
echo.
echo ========================================
echo USEFUL LOG COMMANDS FOR REFERENCE
echo ========================================
echo.
echo Manual log commands you can use:
echo.
echo View all logs:
echo adb logcat
echo.
echo View app-specific logs:
echo adb logcat ^| findstr -i purefocus
echo.
echo View only errors:
echo adb logcat *:E
echo.
echo Save logs to file:
echo adb logcat -d ^> logs.txt
echo.
echo Clear logs:
echo adb logcat -c
echo.
echo Monitor specific tag:
echo adb logcat -s TAG_NAME
echo.
echo Filter by priority (V=Verbose, D=Debug, I=Info, W=Warn, E=Error, F=Fatal):
echo adb logcat *:W
echo.
echo View memory info:
echo adb shell dumpsys meminfo com.neimasilk.purefocus
echo.
echo View app processes:
echo adb shell ps ^| findstr purefocus
echo.
echo Thank you for using PureFocus Log Viewer!
echo.
pause