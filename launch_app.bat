@echo off
echo PureFocus - Launch App on Device
echo ==================================

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
        goto :launch_app
    )
)

echo ❌ ERROR: No device detected or ADB not working
echo.
echo Troubleshooting:
echo 1. Make sure your device is connected via USB
echo 2. Enable USB Debugging in Developer Options
echo 3. Authorize the computer on your device
echo 4. Try: adb kill-server && adb start-server
echo.
echo To install and run the app, use: run_app_to_device.bat
pause
exit /b 1

:launch_app
echo.
echo Checking if PureFocus is installed...
"%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr purefocus

if %ERRORLEVEL% neq 0 (
    echo ❌ PureFocus is not installed on this device.
    echo.
    echo Please run 'run_app_to_device.bat' to install first.
    pause
    exit /b 1
)

echo ✅ PureFocus is installed
echo.
echo Launching PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am start -n com.neimasilk.purefocus/.MainActivity

if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to launch app
    echo.
    echo Debugging options:
    echo 1. Check if app crashed: debug_app_crash.bat
    echo 2. View logs: adb logcat | findstr PureFocus
    echo 3. Reinstall app: run_app_to_device.bat
    echo 4. Check app permissions in device settings
    echo.
    echo You can also manually open PureFocus from your device.
else
    echo ✅ App launched successfully! Check your device.
)

echo.
echo Useful commands:
echo - Debug crashes: debug_app_crash.bat
echo - View logs: adb logcat
echo - Reinstall: run_app_to_device.bat
echo - Uninstall: uninstall_app.bat
echo.
pause