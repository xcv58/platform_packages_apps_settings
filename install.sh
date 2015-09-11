#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Usage: $0 product"
  exit 1
fi
product=$1
apk=out/target/product/$product/system/priv-app/Settings.apk
if [ ! -f "$apk" ]; then
  echo "Apk $apk does not exist"
  exit 1
fi
adb push $apk /sdcard/
if [ $? -ne 0 ]; then
  echo "Fail to push $apk to sdcard"
  exit
fi 
adb root
adb remount
adb shell cp /system/priv-app/Settings.apk /system/priv-app/Settings.apk.bak
adb shell cp /sdcard/Settings.apk /system/priv-app/Settings.apk
adb reboot
