#!/usr/bin/env bash
# Minimal transparent bootstrap (offline build not possible without Gradle/SDK).
# Downloads the public Gradle distribution once and executes Gradle.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERSION="8.11.1"
BASE="${GRADLE_USER_HOME:-${HOME}/.gradle}/taskbar-bootstrap"
TARGET="$BASE/gradle-$VERSION"
if [ ! -x "$TARGET/bin/gradle" ]; then
  command -v curl >/dev/null || { echo "curl is required for first Gradle bootstrap" >&2; exit 1; }
  command -v unzip >/dev/null || { echo "unzip is required for first Gradle bootstrap" >&2; exit 1; }
  mkdir -p "$BASE"
  ZIP="$BASE/gradle-$VERSION-bin.zip"
  curl -fL --retry 2 "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ZIP"
  unzip -oq "$ZIP" -d "$BASE"
  rm -f "$ZIP"
fi
cd "$ROOT"
exec "$TARGET/bin/gradle" "$@"
