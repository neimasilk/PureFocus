@echo off
echo ========================================
echo PureFocus - Quick Launch
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
    echo.
    echo To start emulator, run: run_app_to_emulator.bat
    pause
    exit /b 1
)

echo.
echo Launching PureFocus app...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am start -n com.neimasilk.purefocus/.MainActivity

if %ERRORLEVEL% equ 0 (
    echo ✅ PureFocus launched successfully!
    echo Check your emulator screen.
) else (
    echo ❌ Failed to launch app. 
    echo Make sure PureFocus is installed on the emulator.
    echo Run 'run_app_to_emulator.bat' to install first.
)

echo.
pause