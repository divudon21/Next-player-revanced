# Shizuku authorization lifecycle fix

This change is based on local HDR implementation commit `b4e6bfd`.

## Changes

1. Keep the existing protected, exported `rikka.shizuku.ShizukuProvider` with authority `${applicationId}.shizuku` and the API_V23 uses-permission. Binary inspection of the previous delivered APK confirmed that this provider, its correct debug authority, API_V23 permission and V3_SUPPORT metadata were already packaged. The provider is not added twice; a missing provider alone therefore does not explain the reported behavior.
2. Explicitly declare application metadata `moe.shizuku.client.V3_SUPPORT=true`. Shizuku Manager uses that marker and the API permission to discover compatible apps. The official provider library normally contributes this marker through manifest merging, too; explicitly declaring it removes reliance on an indirect module's manifest contribution, rather than claiming it was absent in the prior APK.
3. Include `implementation("dev.rikka.shizuku:provider:13.1.5")` directly in the app module, as well as retaining the player module's API/provider dependencies.
4. `MainActivity.onCreate()` acquires a shared authorization subscription. `onDestroy()` closes it. Direct external video launches still work without opening MainActivity: the playback manager also subscribes and requests permission when HDR is needed.
5. A shared coordinator in `ShizukuManager.kt` attaches one permission-result listener, a sticky binder-received listener, and a binder-death listener while there are active owners. It removes all three after the last owner closes. No Activity is stored in the coordinator.
6. After a live binder arrives, call `Shizuku.checkSelfPermission()`. Reuse a saved grant immediately. Request with `Shizuku.requestPermission(REQUEST_CODE)` when denied and startup/playback needs authorization. Explicitly inspect `grantResult == PackageManager.PERMISSION_GRANTED` before initializing privileged HDR work.
7. Do not request repeatedly while a prompt is pending or after denial in the same binder session, even if activities are recreated. A new Shizuku binder session resets this prompt-attempt guard. Grant state is not faked or stored in app preferences; Shizuku remains the authority.
8. On binder death, clear stale shell/binding references. Keep the existing HDR restoration journal, shell timeouts, native playback fallback and close/unbind cleanup.

## Persistence and installation

The app cannot force the user's choice to become permanent. Shizuku saves authorization under its own rules. If a permission dialog offers a one-time versus persistent/always-allow grant, choose the persistent option to avoid asking again after a process restart. Denied users can enable access in Shizuku's application list; no automatic denial-prompt loop is introduced.

The supplied APK uses `dev.anilbeesetti.nextplayer.debug` and the same repository debug signing key as the earlier APK. Install it as an update rather than uninstalling the old APK. Uninstall/reinstall, clearing app or Shizuku data, switching Android users/work profiles, changing application ID, UID or signing identity can change/reset authorization. Official Next Player (`dev.anilbeesetti.nextplayer`) is a different package from this debug build, even if the display name is similar.

This structural fix does not guarantee how every Shizuku build/OEM displays or persists apps. Verify on the target phone. If a persistent grant still disappears, collect Shizuku version, Android user/profile, exact installed package ID, and logs before changing display commands or clearing data.

## On-device verification checklist

- Start Shizuku and install the new APK over the old debug build.
- Launch Next Player from its launcher and approve the persistent grant when offered.
- Open Shizuku's applications UI and confirm Next Player's debug package/profile appears as allowed.
- Reopen the app, rotate it, switch between launcher and player, and play multiple HDR clips: no duplicate prompt should occur.
- Kill only the Next Player process, reopen it, and confirm a saved Shizuku grant is reused.
- Restart Shizuku and confirm binder reconnection and saved-grant reuse.
- Deny permission: no shell controller should initialize; normal video playback should continue and repeated player events should not prompt in a loop.
- Open a video directly from a file manager without launching MainActivity first.

No physical device or emulator UI verification is claimed. Build evidence and focused automated test results are included with the deliverable.

## Upstream references inspected

- https://github.com/RikkaApps/Shizuku-API/blob/master/provider/src/main/AndroidManifest.xml
- https://github.com/RikkaApps/Shizuku-API/blob/master/demo/src/main/AndroidManifest.xml
- https://github.com/RikkaApps/Shizuku/blob/master/manager/src/main/java/moe/shizuku/manager/authorization/AuthorizationManager.kt
- https://github.com/RikkaApps/Shizuku/blob/master/server/src/main/java/rikka/shizuku/server/ShizukuService.java
