# Optional Shizuku HDR assistance

Base: anilbeesetti/nextplayer commit `431d34cd33b122f8b5a6e8f49caa65413bbaf88f`.

## What this implementation does

- Adds Shizuku API/provider 13.1.5 to `feature/player/build.gradle.kts` (the module owning playback), the provider/permission to the app manifest, and a release keep rule.
- Observes ExoPlayer's actual `videoFormat.colorInfo` from the service's AnalyticsListener events. Publishes PQ/ST2084 or HLG detection in MediaSession extras, including adaptive input-format changes. The activity consumes those extras and `isPlaying` changes.
- Checks/requests Shizuku permission at MainActivity startup using shared lifecycle-managed listeners; direct video launches can request it when HDR playback needs it. Missing/stopped Shizuku, denied permissions, and unsupported shell commands leave native SurfaceView rendering intact.
- Uses a supported Shizuku UserService, AIDL, and a serialized background worker. Each shell process has a five-second watchdog, drained output pipes, bounded output capture, checked exit status, and explicit destruction. Activity destruction queues restoration outside lifecycleScope, then unbinds the privileged service.
- Merges rather than replaces `global/hdr_enhancer_app_list`, adding YouTube and the actual application ID (including `.debug`). Tries `system/oplus_customize_video_display_enhancement=1` on Oppo/Realme/OnePlus/Oplus and `system/media_hd_enhance=1` on Xiaomi/Redmi/Poco.
- Only tries `cmd display set-hdr-conversion-mode 2` if the device advertises it in `cmd display help` and its prior global conversion mode is readable and safely restorable (1 or 2). Does not blindly send mode 0.
- Requests HDR window color mode on API 26+, and desired headroom 10 on API 35+. Pause, stop, SDR playback, controller release, and invisible/background activity restore default window mode and automatic headroom.
- Journals original device settings before changing them; restores them on disable/close and recovers a leftover journal on a future authorized connection. Compare-and-restore avoids replacing values changed elsewhere after our write. Failed restoration stays journalled.

## Important corrections to the proposed approach

1. `Shizuku.newProcess()` is **private** in the 13.1.5 artifact, not an API Kotlin code can call. The implementation uses Shizuku's supported UserService instead of reflection into private APIs. `HdrShellService.kt` and `IHdrShell.aidl` are necessary additional files.
2. `Window.setDesiredHdrHeadroom()` was added in **API 35**, not 34, and does not control independent SurfaceView/SurfaceControl headroom. It is a hint, not a brightness override.
3. Android HDR conversion enum 2 means system-managed; 0 means unsupported, not universally "restore defaults". A forced-output conversion is not equivalent to panel HDR activation or an OEM whitelist bypass. Shell syntax and availability vary across builds.
4. The three OEM settings are undocumented, firmware-specific probes. A successful `settings put` can merely create an ignored key. Neither Shizuku shell privilege nor these keys guarantees OEM whitelist bypass, panel HBM, wide gamut, max brightness, or Dolby Vision support. No spoofed package identity, root requirement, daemon patch, SELinux bypass, or thermal-limit override is implemented.
5. Dolby Vision may not expose PQ/HLG through every decoder. If the active format's transfer is absent/unknown, this implementation deliberately does not assume HDR solely from a codec label.
6. Immediate restoration cannot be guaranteed after force-stop, process kill, Shizuku death, or permission revocation. Recovery requires relaunching playback with a working, authorized Shizuku connection. An activity lifecycle callback is not a crash handler.

## Build

Use the repository's Gradle wrapper and Android SDK 37 (the wrapper provisions its configured daemon JDK). For example:

```sh
./gradlew :app:assembleDebug
```

The existing ABI splits produce an arm64-v8a APK along with other architectures. Use the arm64-v8a output under `app/build/outputs/apk/debug/`.

This command avoids parallel Kotlin daemons. A 2 GB host also needs swap for DEX processing; lowering the heap too far can stall D8 or cause an out-of-memory failure:

```sh
./gradlew :app:assembleDebug --no-daemon --no-parallel --max-workers=1 \
  -Dorg.gradle.jvmargs='-Xmx1800m -XX:MaxMetaspaceSize=512m -XX:ReservedCodeCacheSize=64m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' \
  -Pkotlin.compiler.execution.strategy=in-process
```

Focused tests:

```sh
./gradlew :feature:player:testDebugUnitTest --tests '*HdrFormatTest'
./gradlew :feature:player:ktlintCheck
```

## Install / device validation

1. Install and start Shizuku using wireless debugging or ADB, then install this debug APK. It has a separate `.debug` application ID and uses the repository's debug signing key, not the project's release key.
2. Play a known HDR10/PQ video; allow the Shizuku permission prompt. Also test an HLG video and SDR control clip.
3. Inspect `adb logcat -s ShizukuHDR`; unsupported/denied overrides are logged. Inspect the three settings before, during, and after playback. Do not interpret a settings row as proof of hardware HDR.
4. Verify the display's real HDR/HBM state using device-specific display diagnostics or measurement. Compare native playback, Shizuku playback, and an OEM-whitelisted player on the same firmware.
5. Check pause/resume, seek, adaptive HDR-to-SDR changes, playlist transition, completion, playback error, rotation, PiP, background playback, activity close, denied permission, stopped Shizuku, and Shizuku restart. Confirm original settings are restored (including deletion of keys originally absent).
6. Test API 24/25 fallback, API 26+ window color mode, API 34 without headroom calls, and API 35+ with headroom hints.

No physical Realme/Xiaomi/Oppo device or emulator validation is claimed. See the delivery build report for actual build and test results.
