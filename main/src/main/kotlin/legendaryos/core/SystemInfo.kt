package legendaryos.core

import java.io.File
import java.lang.management.ManagementFactory

/**
 * Zbiera informacje o systemie Linux (LegendaryOS).
 */
data class SystemInfo(
    val kernelVersion: String,
    val hostname: String,
    val uptime: String,
    val cpuUsage: Float,
    val ramUsage: Float,
    val diskUsage: Float,
    val cpuTemp: Float,
    val androidConnected: Boolean,
    val androidModel: String,
) {
    companion object {
        fun collect(): SystemInfo {
            val kernel = runCommand("uname -r").trim().ifEmpty { "unknown" }
            val hostname = runCommand("hostname").trim().ifEmpty { "legendaryos" }
            val uptime = runCommand("uptime -p").trim().ifEmpty { "n/a" }

            // CPU usage (simple /proc/stat read)
            val cpuUsage = readCpuUsage()

            // RAM
            val ramUsage = readRamUsage()

            // Disk /
            val diskUsage = readDiskUsage("/")

            // CPU temp
            val cpuTemp = readCpuTemp()

            // Android ADB check
            val adbOut = runCommand("adb devices")
            val androidConnected = adbOut.lines().drop(1).any { it.contains("\tdevice") }
            val androidModel = if (androidConnected) {
                runCommand("adb shell getprop ro.product.model").trim().ifEmpty { "Android Device" }
            } else ""

            return SystemInfo(
                kernelVersion = kernel,
                hostname = hostname,
                uptime = uptime,
                cpuUsage = cpuUsage,
                ramUsage = ramUsage,
                diskUsage = diskUsage,
                cpuTemp = cpuTemp,
                androidConnected = androidConnected,
                androidModel = androidModel
            )
        }

        private fun runCommand(cmd: String): String {
            return try {
                val proc = Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", cmd))
                proc.inputStream.bufferedReader().readText()
            } catch (_: Exception) { "" }
        }

        private fun readCpuUsage(): Float {
            return try {
                val bean = ManagementFactory.getOperatingSystemMXBean()
                val load = (bean as? com.sun.management.OperatingSystemMXBean)?.cpuLoad ?: 0.0
                (load * 100).toFloat().coerceIn(0f, 100f)
            } catch (_: Exception) { 0f }
        }

        private fun readRamUsage(): Float {
            return try {
                val lines = File("/proc/meminfo").readLines()
                val total = lines.first { it.startsWith("MemTotal") }.split(Regex("\\s+"))[1].toLong()
                val available = lines.first { it.startsWith("MemAvailable") }.split(Regex("\\s+"))[1].toLong()
                val used = total - available
                (used.toFloat() / total.toFloat() * 100f).coerceIn(0f, 100f)
            } catch (_: Exception) { 0f }
        }

        private fun readDiskUsage(path: String): Float {
            return try {
                val f = File(path)
                val used = f.totalSpace - f.freeSpace
                (used.toFloat() / f.totalSpace.toFloat() * 100f).coerceIn(0f, 100f)
            } catch (_: Exception) { 0f }
        }

        private fun readCpuTemp(): Float {
            val tempFiles = listOf(
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/hwmon/hwmon0/temp1_input"
            )
            for (path in tempFiles) {
                try {
                    val raw = File(path).readText().trim().toFloat()
                    return if (raw > 1000) raw / 1000f else raw
                } catch (_: Exception) {}
            }
            return 0f
        }
    }
}
