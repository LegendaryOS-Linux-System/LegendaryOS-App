package legendaryos.bridge

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * AdbBridge — komunikacja z urządzeniami Android przez ADB.
 * Uruchamia procesy systemowe adb, scrcpy itp.
 */
object AdbBridge {

    private fun run(vararg args: String): String {
        return try {
            val proc = Runtime.getRuntime().exec(args)
            val out = proc.inputStream.bufferedReader().readText().trim()
            val err = proc.errorStream.bufferedReader().readText().trim()
            proc.waitFor()
            if (out.isNotEmpty()) out else if (err.isNotEmpty()) err else "(brak wyjścia)"
        } catch (e: Exception) {
            "Błąd: ${e.message}"
        }
    }

    fun connect(mode: String, ip: String = ""): String {
        return when (mode) {
            "USB" -> {
                val devices = run("adb", "devices")
                if (devices.contains("device\n") || devices.lines().size > 2)
                    "✓ Urządzenie USB wykryte:\n$devices"
                else
                    "⚠ Brak urządzeń USB. Sprawdź USB Debugging."
            }
            "WiFi (ADB Wireless)" -> {
                if (ip.isBlank()) return "⚠ Podaj IP urządzenia"
                val result = run("adb", "connect", ip)
                "$ adb connect $ip\n$result"
            }
            else -> "Tryb '$mode' nieobsługiwany"
        }
    }

    fun listDevices(): String {
        val out = run("adb", "devices", "-l")
        return "$ adb devices -l\n$out"
    }

    fun launchScrcpy() {
        try {
            // Uruchom scrcpy w tle
            ProcessBuilder("scrcpy", "--window-title=LegendaryOS — Mirror", "--stay-awake")
                .redirectErrorStream(true)
                .start()
        } catch (e: Exception) {
            println("scrcpy error: ${e.message}. Zainstaluj: flatpak install flathub info.guardianproject.Scrcpy")
        }
    }

    fun syncClipboard(): String {
        val linuxClip = try {
            run("xclip", "-o", "-selection", "clipboard")
        } catch (_: Exception) {
            run("wl-paste")
        }
        return if (linuxClip.isNotBlank()) {
            run("adb", "shell", "am", "broadcast", "-a", "clipper.set", "--es", "text", "'$linuxClip'")
            "✓ Schowek zsynchronizowany → Android"
        } else {
            "⚠ Schowek Linux jest pusty"
        }
    }

    fun openFileTransfer(): String {
        // Uruchom MTP lub pcmanfm z ADB pull/push UI
        return try {
            ProcessBuilder("pcmanfm", "mtp://").start()
            "✓ Otwarto menedżer plików MTP"
        } catch (_: Exception) {
            "⚠ Zainstaluj pcmanfm lub użyj zakładki 'Pliki'"
        }
    }

    fun enableNotificationBridge(): String {
        // scrcpy does this; alternatively kdeconnect
        return run("bash", "-c", "adb shell settings get secure enabled_notification_listeners")
    }

    fun enableWebcam(): String {
        // Użyj v4l2loopback + adb
        return try {
            ProcessBuilder("bash", "-c",
                "adb forward tcp:4747 tcp:4747 && droidcam-cli adb 4747"
            ).start()
            "✓ Kamera uruchomiona (DroidCam)"
        } catch (_: Exception) {
            "⚠ Zainstaluj droidcam-cli. pkg install droidcam"
        }
    }

    fun openSmsManager(): String {
        // Otwórz KDE Connect lub własny SMS viewer
        return try {
            ProcessBuilder("kdeconnect-sms").start()
            "✓ Otwarto KDE Connect SMS"
        } catch (_: Exception) {
            "⚠ Zainstaluj KDE Connect na Linux i Android"
        }
    }

    fun openAdbShell(): String {
        // Otwórz terminal z adb shell
        return try {
            ProcessBuilder("bash", "-c",
                "konsole -e 'adb shell' || xterm -e 'adb shell' || gnome-terminal -- adb shell"
            ).start()
            "✓ Otwarto ADB Shell"
        } catch (_: Exception) {
            "⚠ Brak emulatora terminala"
        }
    }

    fun enableUsbTethering(): String {
        val result = run("adb", "shell", "svc", "usb", "setFunction", "rndis")
        return "$ adb shell svc usb setFunction rndis\n$result"
    }
}
