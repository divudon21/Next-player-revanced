# Next Player — complete source project

Yeh SOURCE-ONLY bundle hai. Is request ke liye koi APK build nahi kiya gaya aur ZIP mein APK files nahi hain.

## Included
- Complete app/, core/, feature/, tools/ and resources
- Shizuku authorization startup + lifecycle fix
- HDR detection, privileged shell service, AIDL bridge and restoration journal
- Manifest/provider configuration and dependencies
- Gradle wrapper, version catalog, build scripts and tests
- Upstream LICENSE and implementation documentation

## APK configurations already present
app/build.gradle.kts has ABI splitting enabled for APK builds, including:
- arm64-v8a
- armeabi-v7a
- Universal APK (isUniversalApk = true)

The upstream x86 and x86_64 splits are also retained, and the universal APK includes all configured ABIs.
No build commands were executed to prepare this source bundle.

## Open / build later if you choose
Extract the ZIP and open the nextplayer/ folder in Android Studio.
Install the Android SDK required by the project (compile SDK 37). Gradle downloads dependencies and its configured daemon JDK when needed, so an internet connection is required for a fresh environment.
Configure your own local Android SDK path; machine-specific local.properties is intentionally excluded.

Optional future command, NOT executed for this request:
    ./gradlew :app:assembleDebug
On Windows:
    gradlew.bat :app:assembleDebug

The existing split configuration produces these debug outputs in app/build/outputs/apk/debug/:
- app-arm64-v8a-debug.apk
- app-armeabi-v7a-debug.apk
- app-universal-debug.apk
- x86 / x86_64 APKs as well

The repository debug keystore is included as upstream development material. Release distribution requires your own private release signing key; do not treat the public debug key as a secure release key.

## Notes
Shizuku controls authorization persistence; use its persistent grant when offered. OEM HDR shell settings remain firmware-dependent. See SHIZUKU_AUTHORIZATION.md and SHIZUKU_HDR.md for details.

Source commit: f9cc5a0 (includes full prior HDR implementation).
