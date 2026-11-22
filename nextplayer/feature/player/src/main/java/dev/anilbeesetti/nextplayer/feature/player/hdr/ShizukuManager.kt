package dev.anilbeesetti.nextplayer.feature.player.hdr

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import java.util.UUID
import java.util.concurrent.Executors
import org.json.JSONArray
import org.json.JSONObject
import rikka.shizuku.Shizuku

/** Optional, best-effort OEM HDR assistance. All device writes are serialized and journalled. */
class ShizukuManager(context: Context) : AutoCloseable {
    private val app = context.applicationContext
    private val journal = app.getSharedPreferences(
        "hdr_restore",
        Context.MODE_PRIVATE,
    )
    private val main = Handler(Looper.getMainLooper())
    private val args = Shizuku.UserServiceArgs(
        ComponentName(
            app,
            HdrShellService::class.java,
        ),
    )
        .daemon(false)
        .processNameSuffix("hdr_shell")
        .tag("hdr-${UUID.randomUUID()}")
        .version(1)

    @Volatile private var shell: IHdrShell? = null
    private var desired = false
    private var closed = false
    private var binding = false

    // Accessed only on worker.
    private var applied = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            shell = IHdrShell.Stub.asInterface(service)
            if (closed) {
                releaseService()
            } else {
                // Also recover a previous process's unfinished restoration.
                worker.execute { applied = false }
                enqueue(desired)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            shell = null
            binding = false
            // Keep the journal for recovery after Shizuku is restarted.
        }
    }
    private val authorization = ShizukuAuthorization.acquire(
        onUnavailable = {
            shell = null
            binding = false
        },
    ) {
        // Only invoked after Shizuku confirms a grant, including an existing saved grant.
        if (!closed) connectIfAllowed()
    }

    /** Call on the main thread. Reuses startup authorization, or requests it for direct playback. */
    fun enableHardwareHDR(enable: Boolean) {
        if (closed) return
        if (desired == enable) {
            if (enable) {
                ShizukuAuthorization.requestPermission()
                connectIfAllowed()
            }
            return
        }
        desired = enable
        if (enable) {
            ShizukuAuthorization.requestPermission()
            connectIfAllowed()
        }
        if (shell != null) enqueue(enable)
    }

    private fun connectIfAllowed() {
        if (closed || binding) return
        runCatching {
            if (!Shizuku.pingBinder()) return
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) return
            // Connect even when paused so a journal left after a crash can be restored.
            binding = true
            Shizuku.bindUserService(
                args,
                connection,
            )
        }.onFailure {
            binding = false
            Log.w(
                TAG,
                "Cannot connect to HDR shell",
                it,
            )
        }
    }

    private fun enqueue(enable: Boolean) {
        worker.execute {
            runCatching {
                if (enable) {
                    if (!applied && restore()) {
                        applyOverrides()
                        applied = true
                    }
                } else {
                    restore()
                    applied = false
                }
            }.onFailure {
                Log.w(
                    TAG,
                    "HDR assistance failed; native rendering remains available",
                    it,
                )
            }
        }
    }

    private data class Result(val code: Int, val output: String)

    private fun execute(command: String): Result {
        val response = checkNotNull(shell) { "Shizuku service disconnected" }.execute(command)
        val result = Result(
            response.substringBefore('\n').toIntOrNull() ?: -1,
            response.substringAfter(
                '\n',
                "",
            ).trim(),
        )
        check(result.code == 0) { "$command: ${result.output}" }
        return result
    }

    private fun read(namespace: String, key: String): String? =
        execute("settings get $namespace $key").output.takeUnless { it == "null" }

    private fun save(records: JSONArray) {
        check(
            journal.edit().putString(
                "pending",
                records.toString(),
            ).commit(),
        ) {
            "Cannot save HDR restore journal"
        }
    }

    private fun overrideSetting(
        namespace: String,
        key: String,
        value: String,
        display: Boolean = false,
    ) {
        val previous = read(
            namespace,
            key,
        )
        if (previous == value) return
        val records = JSONArray(
            journal.getString(
                "pending",
                "[]",
            ),
        )
        records.put(
            JSONObject()
                .put(
                    "namespace",
                    namespace,
                )
                .put(
                    "key",
                    key,
                )
                .put(
                    "old",
                    previous ?: JSONObject.NULL,
                )
                .put(
                    "applied",
                    value,
                )
                .put(
                    "display",
                    display,
                ),
        )
        // Write ahead: a process death between the shell call and its reply is recoverable.
        save(records)
        execute(
            if (display) {
                "cmd display set-hdr-conversion-mode $value"
            } else {
                "settings put $namespace $key ${quote(value)}"
            },
        )
    }

    private fun applyOverrides() {
        fun attempt(block: () -> Unit) {
            runCatching(block).onFailure {
                Log.w(
                    TAG,
                    "OEM HDR override unsupported or denied",
                    it,
                )
            }
        }
        attempt {
            val existing = read(
                "global",
                "hdr_enhancer_app_list",
            )
            val packages = existing.orEmpty().split(',').filter { it.isNotBlank() }.toMutableSet()
            packages.add("com.google.android.youtube")
            packages.add(app.packageName)
            overrideSetting(
                "global",
                "hdr_enhancer_app_list",
                packages.joinToString(","),
            )
        }
        val manufacturer = Build.MANUFACTURER.lowercase(java.util.Locale.ROOT)
        if (manufacturer in setOf(
                "oppo",
                "realme",
                "oneplus",
                "oplus",
            )
        ) {
            attempt {
                overrideSetting(
                    "system",
                    "oplus_customize_video_display_enhancement",
                    "1",
                )
            }
        }
        if (manufacturer in setOf(
                "xiaomi",
                "redmi",
                "poco",
            )
        ) {
            attempt {
                overrideSetting(
                    "system",
                    "media_hd_enhance",
                    "1",
                )
            }
        }
        attempt {
            // Not a portable AOSP shell command. Never guess the previous conversion state.
            val help = execute("cmd display help").output
            val mode = read(
                "global",
                "hdr_conversion_mode",
            )
            if (help.contains("set-hdr-conversion-mode") && mode in setOf(
                    "1",
                    "2",
                )
            ) {
                overrideSetting(
                    "global",
                    "hdr_conversion_mode",
                    "2",
                    display = true,
                )
            }
        }
    }

    /** Compare-and-restore avoids overwriting settings subsequently changed by the user. */
    private fun restore(): Boolean {
        val records = JSONArray(
            journal.getString(
                "pending",
                "[]",
            ),
        )
        val failed = JSONArray()
        for (index in records.length() - 1 downTo 0) {
            val record = records.getJSONObject(index)
            runCatching {
                val namespace = record.getString("namespace")
                val key = record.getString("key")
                if (read(
                        namespace,
                        key,
                    ) == record.getString("applied")
                ) {
                    val previous = if (record.isNull("old")) null else record.getString("old")
                    execute(
                        when {
                            record.getBoolean(
                                "display",
                            ) -> "cmd display set-hdr-conversion-mode $previous"

                            previous == null -> "settings delete $namespace $key"

                            else -> "settings put $namespace $key ${quote(previous)}"
                        },
                    )
                }
            }.onFailure {
                failed.put(record)
                Log.w(
                    TAG,
                    "HDR restoration deferred until Shizuku reconnects",
                    it,
                )
            }
        }
        save(failed)
        return failed.length() == 0
    }

    override fun close() {
        if (closed) return
        closed = true
        desired = false
        authorization.close()
        // Deliberately not lifecycleScope: restoration must outlive Activity.onDestroy().
        worker.execute {
            try {
                restore()
            } catch (error: Exception) {
                Log.w(
                    TAG,
                    "HDR restoration journal retained",
                    error,
                )
            } finally {
                main.post { releaseService() }
            }
        }
    }

    private fun releaseService() {
        runCatching {
            Shizuku.unbindUserService(
                args,
                connection,
                true,
            )
        }
        shell = null
        binding = false
    }

    companion object {
        /** Attach from MainActivity.onCreate; close the returned handle in onDestroy. */
        fun initializeAuthorization(): AutoCloseable =
            ShizukuAuthorization.acquire(requestAtStartup = true) {}

        private const val TAG = "ShizukuHDR"
        private val worker = Executors.newSingleThreadExecutor { task ->
            Thread(
                task,
                "hdr-settings",
            ).apply { isDaemon = true }
        }
        private fun quote(value: String): String = "'" + value.replace(
            "'",
            "'\\''",
        ) + "'"
    }
}

/** One permission request per binder session, shared across Activity recreation and players. */
internal class ShizukuPermissionRequestState {
    private var attempted = false
    var inFlight = false
        private set

    fun beginRequest(alreadyGranted: Boolean): Boolean {
        if (alreadyGranted || attempted) return false
        attempted = true
        inFlight = true
        return true
    }

    fun finishRequest() {
        inFlight = false
    }

    fun resetForNewBinder() {
        attempted = false
        inFlight = false
    }
}

/**
 * Shared permission listeners; never stores an Activity or an authorization flag in preferences.
 * The Shizuku server is the authority for saved grants. All operations run on the main thread.
 */
private object ShizukuAuthorization {
    private const val TAG = "ShizukuAuthorization"
    private const val REQUEST_CODE = 4812
    private val main = Handler(Looper.getMainLooper())
    private val requestState = ShizukuPermissionRequestState()
    private data class Observer(
        val requestAtStartup: Boolean,
        val onUnavailable: () -> Unit,
        val onGranted: () -> Unit,
    )
    private val observers = mutableMapOf<Any, Observer>()
    private var attached = false
    private var requestForPlayback = false
    private var lastBinder: IBinder? = null

    private val permissionListener = Shizuku.OnRequestPermissionResultListener {
            code,
            grantResult,
        ->
        main.post {
            if (code == REQUEST_CODE && attached) {
                requestState.finishRequest()
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    notifyGranted()
                } else {
                    // A denial must never start a privileged HDR controller or prompt in a loop.
                    Log.i(
                        TAG,
                        "Permission denied; authorize Next Player in Shizuku to enable HDR assistance",
                    )
                }
            }
        }
    }
    private val receivedListener = Shizuku.OnBinderReceivedListener {
        main.post {
            if (attached) {
                updateBinderSession()
                evaluatePermission()
            }
        }
    }
    private val deadListener = Shizuku.OnBinderDeadListener {
        main.post {
            if (attached) {
                lastBinder = null
                requestState.resetForNewBinder()
                observers.values.toList().forEach { it.onUnavailable() }
            }
        }
    }

    fun acquire(
        requestAtStartup: Boolean = false,
        onUnavailable: () -> Unit = {},
        onGranted: () -> Unit,
    ): AutoCloseable {
        checkMainThread()
        val token = Any()
        observers[token] = Observer(
            requestAtStartup,
            onUnavailable,
            onGranted,
        )
        attach()
        // Defer callbacks until the owning manager's constructor has finished.
        main.post { if (token in observers) evaluatePermission() }
        return AutoCloseable {
            checkMainThread()
            observers.remove(token)
            if (observers.isEmpty()) detach()
        }
    }

    fun requestPermission() {
        checkMainThread()
        requestForPlayback = true
        evaluatePermission()
    }

    private fun attach() {
        if (attached) return
        runCatching {
            attached = true
            Shizuku.addRequestPermissionResultListener(permissionListener)
            Shizuku.addBinderDeadListener(deadListener)
            Shizuku.addBinderReceivedListenerSticky(receivedListener)
        }.onFailure {
            detach()
            Log.i(
                TAG,
                "Shizuku not available; native playback remains available",
                it,
            )
        }
    }

    private fun updateBinderSession() {
        val binder = Shizuku.getBinder()
        if (binder != lastBinder) {
            lastBinder = binder
            requestState.resetForNewBinder()
        }
    }

    private fun evaluatePermission() {
        if (!attached || observers.isEmpty()) return
        runCatching {
            if (!Shizuku.pingBinder()) return
            updateBinderSession()
            val grant = Shizuku.checkSelfPermission()
            if (grant == PackageManager.PERMISSION_GRANTED) {
                requestState.finishRequest()
                notifyGranted()
            } else if (grant == PackageManager.PERMISSION_DENIED &&
                (requestForPlayback || observers.values.any { it.requestAtStartup }) &&
                requestState.beginRequest(alreadyGranted = false)
            ) {
                // Never bypass a denial or synthesize/cache a grant locally.
                Shizuku.requestPermission(REQUEST_CODE)
            }
        }.onFailure {
            requestState.finishRequest()
            Log.w(
                TAG,
                "Cannot check/request Shizuku authorization",
                it,
            )
        }
    }

    private fun notifyGranted() {
        observers.values.toList().forEach { observer ->
            runCatching { observer.onGranted() }
                .onFailure {
                    Log.w(
                        TAG,
                        "Cannot initialize authorized HDR controller",
                        it,
                    )
                }
        }
    }

    private fun detach() {
        attached = false
        requestForPlayback = false
        // Keep the attempt state across Activity recreation to avoid repeated denial prompts.
        runCatching { Shizuku.removeRequestPermissionResultListener(permissionListener) }
        runCatching { Shizuku.removeBinderReceivedListener(receivedListener) }
        runCatching { Shizuku.removeBinderDeadListener(deadListener) }
    }

    private fun checkMainThread() {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Shizuku authorization must run on the main thread"
        }
    }
}
