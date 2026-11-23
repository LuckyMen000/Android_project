#!/usr/bin/env bash
set -euo pipefail
./gradlew :app:assembleDebug :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageVerify
printf '\nReports: app/build/reports/jacoco/rgr3/index.html\n'
if command -v adb >/dev/null && adb devices | grep -qE '^.+[[:space:]]+device$'; then
  ./gradlew :app:connectedDebugAndroidTest :app:rgr3CombinedCoverageReport
else
  echo 'No connected Android device. Run :app:connectedDebugAndroidTest later.'
fi
