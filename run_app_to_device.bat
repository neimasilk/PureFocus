@echo off
echo PureFocus - Run App to Physical Device
echo ========================================

REM Set Android SDK path
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

REM Check if Android SDK exists
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    echo Please install Android Studio or Android SDK first.
    echo.
    echo Troubleshooting:
    echo 1. Check if Android Studio is installed
    echo 2. Verify ANDROID_HOME path: %ANDROID_HOME%
    echo 3. Try running Android Studio and install SDK components
    pause
    exit /b 1
)

echo [1/6] Checking ADB server status...
echo Starting ADB server if needed...
"%ANDROID_HOME%\platform-tools\adb.exe" start-server

echo.
echo [2/6] Checking connected devices...
echo Available devices:
"%ANDROID_HOME%\platform-tools\adb.exe" devices -l

REM Check if any device is connected
for /f "skip=1 tokens=1" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" devices') do (
    if not "%%i"=="" (
        echo ✅ Device detected: %%i
        goto :device_found
    )
)

echo ❌ ERROR: No device detected!
echo.
echo Troubleshooting steps:
echo 1. Make sure your device is connected via USB
echo 2. Enable USB Debugging in Developer Options:
echo    - Go to Settings ^> About Phone
echo    - Tap Build Number 7 times to enable Developer Options
echo    - Go to Settings ^> Developer Options
echo    - Enable USB Debugging
echo 3. Check if device drivers are installed
echo 4. Try different USB cable or port
echo 5. Authorize the computer on your device when prompted
echo 6. Try running: adb kill-server ^&^& adb start-server
echo.
pause
exit /b 1

:device_found
echo.
echo [3/6] Getting device information...
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.model 2^>nul') do set DEVICE_MODEL=%%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.release 2^>nul') do set ANDROID_VERSION=%%i
echo Device Model: %DEVICE_MODEL%
echo Android Version: %ANDROID_VERSION%

echo.
echo [4/6] Building application...
echo This may take a few minutes...
.\gradlew assembleDebug

if %ERRORLEVEL% neq 0 (
    echo ❌ Build failed! 
    echo.
    echo Debugging build errors:
    echo 1. Check if all dependencies are downloaded
    echo 2. Verify internet connection for dependency downloads
    echo 3. Try cleaning project: .\gradlew clean
    echo 4. Check for syntax errors in code
    echo 5. Verify Gradle and JDK versions are compatible
    echo.
    echo Common solutions:
    echo - Run: .\gradlew clean assembleDebug
    echo - Check build.gradle files for errors
    echo - Ensure proper internet connection
    pause
    exit /b 1
)

echo ✅ Build successful!
echo.
echo [5/6] Installing APK to device...
echo Installing PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk

if %ERRORLEVEL% neq 0 (
    echo ❌ Installation failed!
    echo.
    echo Debugging installation errors:
    echo 1. Device storage might be full
    echo 2. App signature conflicts (try uninstalling first)
    echo 3. USB connection issues
    echo 4. Developer options might be disabled
    echo.
    echo Solutions to try:
    echo - Free up device storage
    echo - Run: adb uninstall com.neimasilk.purefocus
    echo - Reconnect USB cable
    echo - Check USB debugging is still enabled
    echo - Try: adb kill-server ^&^& adb start-server
    pause
    exit /b 1
)

echo ✅ Installation successful!
echo.
echo [6/6] Launching PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am start -n com.neimasilk.purefocus/.MainActivity

if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to launch app
    echo.
    echo Debugging launch errors:
    echo 1. App might not be properly installed
    echo 2. MainActivity might not exist or renamed
    echo 3. App might have crashed on startup
    echo.
    echo Manual launch options:
    echo - Open PureFocus manually from device app drawer
    echo - Check logcat for crash details: adb logcat
    echo - Verify app installation: adb shell pm list packages | findstr purefocus
else
    echo ✅ App launched successfully! Check your device.
fi

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo Useful debugging commands:
echo - View real-time logs: adb logcat
echo - Filter app logs: adb logcat | findstr PureFocus
echo - Check app installation: adb shell pm list packages | findstr purefocus
echo - Uninstall app: adb uninstall com.neimasilk.purefocus
echo - Launch app again: adb shell am start -n com.neimasilk.purefocus/.MainActivity
echo - Check device info: adb shell getprop
echo - Screenshot: adb exec-out screencap -p ^> screenshot.png
echo.
echo If app crashes, run: debug_app_crash.bat
echo.
pause