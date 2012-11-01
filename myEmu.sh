#!/bin/sh

~/android-sdk/tools/emulator-arm -avd myAndroid -netspeed full -netdelay none &
# ~/android-sdk/tools/emulator-arm -avd myPad -netspeed full -netdelay none &
~/android-sdk/platform-tools/adb logcat
