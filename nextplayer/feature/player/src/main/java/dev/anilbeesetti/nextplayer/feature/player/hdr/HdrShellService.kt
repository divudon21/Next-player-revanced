package dev.anilbeesetti.nextplayer.feature.player.hdr

import androidx.annotation.Keep
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/** Runs as Shizuku's shell UID, not the application's UID. */
@Keep
class HdrShellService : IHdrShell.Stub() {
    private val watchdog = Executors.newSingleThreadScheduledExecutor()

    @Synchronized
    override fun execute(command: String): String {
        val process = ProcessBuilder(
            "/system/bin/sh",
            "-c",
            command,
        )
            .redirectErrorStream(true)
            .start()
        val timeout = watchdog.schedule(
            { process.destroy() },
            5,
            TimeUnit.SECONDS,
        )
        return try {
            process.outputStream.close()
            // Drain the pipe even after the result buffer is full to avoid deadlocks.
            val output = StringBuilder()
            process.inputStream.bufferedReader().use { reader ->
                val buffer = CharArray(1024)
                while (true) {
                    val count = reader.read(buffer)
                    if (count < 0) break
                    val remaining = 16384 - output.length
                    if (remaining > 0) {
                        output.append(
                            buffer,
                            0,
                            minOf(
                                count,
                                remaining,
                            ),
                        )
                    }
                }
            }
            "${process.waitFor()}\n$output"
        } catch (error: Exception) {
            "-1\n${error.message}"
        } finally {
            timeout.cancel(false)
            process.destroy()
            process.inputStream.close()
            process.errorStream.close()
        }
    }

    override fun destroy() {
        watchdog.shutdownNow()
        exitProcess(0)
    }
}
