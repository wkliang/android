#!/bin/sh

~/android-sdk/tools/emulator-arm -avd myAndroid -netspeed full -netdelay none -logcat "*"
# ~/android-sdk/tools/emulator-arm -avd Nexus_S -netspeed full -netdelay none -logcat "*"
# ~/android-sdk/tools/emulator-arm -avd myPad -netspeed full -netdelay none &
# /home/wkliang/android-sdk/tools/emulator-arm -avd myPad -netspeed full -netdelay none -no-boot-anim -wipe-data -logcat "*"
# ~/android-sdk/platform-tools/adb logcat
