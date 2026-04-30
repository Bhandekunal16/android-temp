#!/bin/bash

compiler="./gradlew"
command=$(emulator -list-avds | head -n 1)

build() {
    ktlint --format && 
    adb uninstall com.example.myapp >/dev/null 2>&1 || true &
    $compiler clean &&
    $compiler build --stacktrace &&
    $compiler assembleDebug &&
    $compiler installDebug
}

if [ "$1" = "build" ]; then
    build

elif [ "$1" = "startup" ]; then
    gradle wrapper

elif [ "$1" = "run" ]; then 
    emulator -avd "$command" -gpu swiftshader_indirect

else
    echo "unknown command"
fi