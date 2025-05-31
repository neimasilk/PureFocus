@echo off
echo ========================================
echo PureFocus - Uninstall from Emulator
echo ========================================
echo.

REM Set Android environment variables
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

echo Checking emulator connection...
"%ANDROID_HOME%\platform-tools\adb.exe" devices
if %ERRORLEVEL% neq 0 (
    echo ERROR: No emulator detected or ADB not working
    echo Please make sure emulator is running first.
    pause
    exit /b 1
)

echo.
echo Checking if PureFocus is installed...
"%ANDROID_HOME%\platform-tools\adb.exe" shell pm list packages | findstr purefocus
if %ERRORLEVEL% neq 0 (
    echo PureFocus is not installed on this emulator.
    echo Nothing to uninstall.
    pause
    exit /b 0
)

echo.
echo Uninstalling PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" uninstall com.neimasilk.purefocus

if %ERRORLEVEL% equ 0 (
    echo ✅ PureFocus uninstalled successfully!
) else (
    echo ❌ Failed to uninstall PureFocus.
    echo The app might not be installed or there was an error.
)

echo.
pause