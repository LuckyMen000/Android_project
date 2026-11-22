# Minimal transparent Gradle bootstrap, without a bundled wrapper JAR.
$ErrorActionPreference = 'Stop'
$version = '8.11.1'
$base = if ($env:GRADLE_USER_HOME) { Join-Path $env:GRADLE_USER_HOME 'taskbar-bootstrap' } else { Join-Path $env:USERPROFILE '.gradle\taskbar-bootstrap' }
$gradleExe = Join-Path $base "gradle-$version\bin\gradle.bat"
try {
    if (!(Test-Path $gradleExe)) {
        New-Item -ItemType Directory -Path $base -Force | Out-Null
        $zip = Join-Path $base "gradle-$version-bin.zip"
        Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-$version-bin.zip" -OutFile $zip
        Expand-Archive -LiteralPath $zip -DestinationPath $base -Force
        Remove-Item -LiteralPath $zip -Force
    }
    Push-Location $PSScriptRoot
    try {
        & $gradleExe @args
        exit $LASTEXITCODE
    } finally {
        Pop-Location
    }
} catch {
    Write-Error $_
    exit 1
}
