#!/usr/bin/env bash
set -euo pipefail
# Set FTL_DEVICE_1, FTL_VERSION_1, FTL_DEVICE_2, FTL_VERSION_2 after inspecting model availability.
: "${FTL_DEVICE_1:?Run gcloud firebase test android models list and set FTL_DEVICE_1}"
: "${FTL_VERSION_1:?Set FTL_VERSION_1}"
: "${FTL_DEVICE_2:?Set FTL_DEVICE_2}"
: "${FTL_VERSION_2:?Set FTL_VERSION_2}"
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest

gcloud firebase test android run \
  --type instrumentation \
  --app app/build/outputs/apk/debug/app-debug.apk \
  --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device "model=${FTL_DEVICE_1},version=${FTL_VERSION_1},locale=ru,orientation=portrait" \
  --device "model=${FTL_DEVICE_2},version=${FTL_VERSION_2},locale=ru,orientation=portrait" \
  --timeout 20m
