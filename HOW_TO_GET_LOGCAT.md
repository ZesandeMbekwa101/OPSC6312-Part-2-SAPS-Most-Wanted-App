# How to Get Logcat Errors

## Method 1: Using Android Studio

1. Open Android Studio
2. Connect your device or start the emulator
3. Click on the **Logcat** tab at the bottom of Android Studio
4. Filter by your app package: `com.example.sapsmostwantedapp`
5. Look for errors in red (they usually say "FATAL EXCEPTION" or "ERROR")
6. Copy the error message and share it

## Method 2: Using ADB Command Line

1. Connect your device via USB
2. Open Command Prompt (Windows) or Terminal (Mac/Linux)
3. Run this command:
   ```
   adb logcat | findstr "sapsmostwantedapp"
   ```
   (On Mac/Linux, use `grep` instead of `findstr`)

4. Try to login as admin
5. When it crashes, you'll see the error in the terminal
6. Copy the error message

## Method 3: Using ADB to Save Logs to File

1. Connect your device
2. Run this command:
   ```
   adb logcat > logcat.txt
   ```
3. Try to login as admin
4. When it crashes, press Ctrl+C to stop logging
5. Open `logcat.txt` and look for errors
6. Share the error message

## What to Look For

Look for lines that contain:
- "FATAL EXCEPTION"
- "AndroidRuntime"
- "crash" or "crash"
- "Exception" or "Error"
- The package name: `com.example.sapsmostwantedapp`

## Quick Test

To test if logcat is working, run:
```
adb logcat -c
```
This clears the log, then try to login and see what appears.


