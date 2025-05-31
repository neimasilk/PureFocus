@echo off
echo ========================================
echo PureFocus - Stop Emulator
echo ========================================
echo.

REM Set Android environment variables
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

echo Checking for running emulators...
"%ANDROID_HOME%\platform-tools\adb.exe" devices

echo.
echo Stopping all emulators...
"%ANDROID_HOME%\platform-tools\adb.exe" emu kill

if %ERRORLEVEL% equ 0 (
    echo ✅ Emulator stopped successfully!
    echo You can now run run_app_to_emulator.bat to start fresh.
) else (
    echo ❌ No emulator was running or failed to stop.
    echo This is normal if no emulator was active.
)

echo.
echo Alternative method - Force kill emulator processes:
echo If emulator is still running, you can manually kill it via Task Manager
echo Look for "qemu-system-x86_64.exe" processes

echo.
pause