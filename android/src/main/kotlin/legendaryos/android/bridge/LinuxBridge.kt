package legendaryos.android.bridge

import kotlinx.coroutines.*

/**
 * LinuxBridge — wysyła polecenia z Androida do LegendaryOS przez sieć (SSH / REST API).
 */
object LinuxBridge {
    private var host: String = "192.168.1.10"
    private var port: Int = 2222

    fun configure(host: String, port: Int) {
        this.host = host
        this.port = port
    }

    /**
     * Wysyła polecenie do LegendaryOS przez SSH lub prosty TCP daemon.
     */
    fun sendCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsch = com.jcraft.jsch.JSch()
                val session = jsch.getSession("legendaryos", host, port)
                session.setPassword("") // klucz SSH preferowany
                session.setConfig("StrictHostKeyChecking", "no")
                session.connect(5000)

                val channel = session.openChannel("exec") as com.jcraft.jsch.ChannelExec
                val shellCommand = when (command) {
                    "Zablokuj ekran"    -> "loginctl lock-session"
                    "Uśpij komputer"   -> "systemctl suspend"
                    "Wyłącz"           -> "systemctl poweroff"
                    "Restart"          -> "systemctl reboot"
                    "bootc upgrade"    -> "bootc upgrade"
                    "Wymuś aktualizację" -> "bootc upgrade --force"
                    else               -> command
                }

                channel.setCommand(shellCommand)
                channel.connect()
                channel.disconnect()
                session.disconnect()
            } catch (e: Exception) {
                android.util.Log.e("LinuxBridge", "Error: ${e.message}")
            }
        }
    }
}

/**
 * NotificationBridgeService — przechwytuje powiadomienia Android i wysyła je do Linux.
 */
class NotificationBridgeService : android.app.Service() {
    override fun onBind(intent: android.content.Intent?) = null
    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        // Powiadomienia z Android → LegendaryOS App (desktop)
        return START_STICKY
    }
}

/**
 * SmsReceiver — odbiera SMS-y i przekazuje je do Linux.
 */
class SmsReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
        val bundle = intent?.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return
        pdus.forEach { pdu ->
            val sms = android.telephony.SmsMessage.createFromPdu(pdu as ByteArray)
            val from = sms.displayOriginatingAddress
            val body = sms.messageBody
            android.util.Log.d("SmsReceiver", "SMS od $from: $body")
            // TODO: wysłać do LegendaryOS przez LinuxBridge
        }
    }
}
