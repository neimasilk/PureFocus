@echo off
echo PureFocus - Physical Device Setup Guide
echo =========================================
echo.
echo This guide will help you set up your Android device for development.
echo.
echo ========================================
echo STEP 1: Enable Developer Options
echo ========================================
echo.
echo 1. Open Settings on your Android device
echo 2. Scroll down and tap "About Phone" or "About Device"
echo 3. Find "Build Number" and tap it 7 times
echo 4. You should see a message saying "You are now a developer!"
echo.
echo Press any key when you have completed Step 1...
pause >nul
echo.
echo ========================================
echo STEP 2: Enable USB Debugging
echo ========================================
echo.
echo 1. Go back to main Settings
echo 2. Look for "Developer Options" (usually under System or Advanced)
echo 3. Tap "Developer Options"
echo 4. Find "USB Debugging" and turn it ON
echo 5. You may see a warning - tap "OK" to confirm
echo.
echo Press any key when you have completed Step 2...
pause >nul
echo.
echo ========================================
echo STEP 3: Connect Device to Computer
echo ========================================
echo.
echo 1. Connect your device to this computer using a USB cable
echo 2. Make sure to use a data cable (not just charging cable)
echo 3. On your device, you may see "USB Debugging" authorization dialog
echo 4. Check "Always allow from this computer" and tap "OK"
echo.
echo Press any key when your device is connected...
pause >nul
echo.
echo ========================================
echo STEP 4: Testing Connection
echo ========================================
echo.
echo Testing ADB connection...

REM Set Android SDK path
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

REM Check if Android SDK exists
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo ❌ ERROR: Android SDK not found!
    echo.
    echo Please install Android Studio first:
    echo 1. Download from: https://developer.android.com/studio
    echo 2. Install Android Studio
    echo 3. Open Android Studio and install SDK components
    echo 4. Run this script again
    pause
    exit /b 1
)

echo Checking for connected devices...
"%ANDROID_HOME%\platform-tools\adb.exe" devices

REM Check if any device is connected
for /f "skip=1 tokens=1" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" devices') do (
    if not "%%i"=="" (
        echo.
        echo ✅ SUCCESS! Device detected: %%i
        goto :device_info
    )
)

echo.
echo ❌ No device detected!
echo.
echo Troubleshooting:
echo 1. Make sure USB Debugging is enabled
echo 2. Check if you authorized the computer on your device
echo 3. Try unplugging and reconnecting the USB cable
echo 4. Try a different USB port or cable
echo 5. Restart ADB: adb kill-server && adb start-server
echo.
echo If problems persist:
echo - Check device manufacturer's USB drivers
echo - Try enabling "PTP" or "File Transfer" mode on device
echo - Disable and re-enable USB Debugging
echo.
pause
exit /b 1

:device_info
echo.
echo ========================================
echo DEVICE INFORMATION
echo ========================================
echo.
echo Getting device details...
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.model 2^>nul') do echo Device Model: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.manufacturer 2^>nul') do echo Manufacturer: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.release 2^>nul') do echo Android Version: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.sdk 2^>nul') do echo API Level: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.cpu.abi 2^>nul') do echo CPU Architecture: %%i

echo.
echo ========================================
echo SETUP COMPLETE!
echo ========================================
echo.
echo Your device is now ready for development!
echo.
echo Available commands:
echo - Install and run app: run_app_to_device.bat
echo - Launch app: launch_app.bat
echo - Run tests: run_instrumented_tests.bat
echo - Debug crashes: debug_app_crash.bat
echo - Uninstall app: uninstall_app.bat
echo.
echo ========================================
echo OPTIONAL: Additional Developer Settings
echo ========================================
echo.
echo For better development experience, you can also enable:
echo.
echo In Developer Options:
echo - Stay Awake (keeps screen on while charging)
echo - Show CPU usage (shows performance overlay)
echo - Don't keep activities (helps test app lifecycle)
echo - Background process limit (helps test memory management)
echo.
echo In Settings ^> Display:
echo - Keep screen timeout longer during development
echo.
echo Press any key to finish setup...
pause >nul
echo.
echo Setup complete! You can now start developing with PureFocus.
echo.