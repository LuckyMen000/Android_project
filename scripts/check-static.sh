#!/usr/bin/env bash
set -euo pipefail
./gradlew :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageVerify
