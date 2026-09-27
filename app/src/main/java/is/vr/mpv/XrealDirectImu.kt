package `is`.vr.mpv

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log

/**
 * Reads the Xreal One IMU directly over USB, bypassing the crashing NRSDK
 * (libnr_service.so). Interface 0 is the glasses' HID sensor/control channel
 * (IN endpoint 0x81, OUT 0x01, 1024-byte interrupt transfers).
 *
 * This is currently a CAPTURE scaffold: it claims the interface and logs the
 * raw bytes arriving on 0x81 so the packet format can be decoded from real
 * data. Once the layout is known, decoded samples are delivered via onSample.
 */
class XrealDirectImu(
    private val context: Context,
    private val onSample: ((IMUData, Long) -> Unit)? = null,
) {
    private companion object {
        const val TAG = "XrealDirectImu"
        const val VENDOR_ID = 13080          // 0x3318
        const val IMU_INTERFACE_ID = 0       // HID sensor/control interface
        const val READ_TIMEOUT_MS = 20
    }

    @Volatile private var running = false
    private var thread: Thread? = null
    private var connection: UsbDeviceConnection? = null
    private var iface: UsbInterface? = null

    fun isRunning() = running

    fun start(): Boolean {
        if (running) return true
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val device = usbManager.deviceList.values.firstOrNull { it.vendorId == VENDOR_ID }
        if (device == null) {
            Log.w(TAG, "No Xreal device present")
            return false
        }
        if (!usbManager.hasPermission(device)) {
            Log.w(TAG, "No USB permission for Xreal device")
            return false
        }

        val intf = findInterface(device) ?: run {
            Log.w(TAG, "IMU interface $IMU_INTERFACE_ID not found")
            return false
        }
        val inEp = findEndpoint(intf, UsbConstants.USB_DIR_IN) ?: run {
            Log.w(TAG, "No IN endpoint on interface ${intf.id}")
            return false
        }
        val outEp = findEndpoint(intf, UsbConstants.USB_DIR_OUT)

        val conn = usbManager.openDevice(device) ?: run {
            Log.w(TAG, "openDevice failed")
            return false
        }
        if (!conn.claimInterface(intf, true)) {
            Log.w(TAG, "claimInterface ${intf.id} failed")
            conn.close()
            return false
        }

        connection = conn
        iface = intf
        running = true
        Log.i(TAG, "Claimed interface ${intf.id}; IN=0x%02x OUT=%s".format(
            inEp.address, outEp?.let { "0x%02x".format(it.address) } ?: "none"))

        dumpDescriptors(conn, intf)
        tryStandardHidEnable(conn, intf)

        thread = Thread { readLoop(conn, inEp, outEp) }.also { it.start() }
        return true
    }

    /** Standard HID requests that can wake a vendor interrupt-IN stream. */
    private fun tryStandardHidEnable(conn: UsbDeviceConnection, intf: UsbInterface) {
        // SET_IDLE(duration=0, report=0): report only on change / stream freely.
        val idle = conn.controlTransfer(0x21, 0x0A, 0x0000, intf.id, null, 0, 1000)
        Log.i(TAG, "SET_IDLE -> $idle")
        // GET_REPORT(Input, id 0): log the returned payload to inspect the format.
        val buf = ByteArray(1024)
        val get = conn.controlTransfer(0xA1, 0x01, 0x0100, intf.id, buf, buf.size, 1000)
        Log.i(TAG, "GET_REPORT(Input) -> $get")
        if (get > 0) logHex("GET_REPORT payload", buf, get)
    }

    /** Fetch and log the HID report descriptor so the report layout can be decoded. */
    private fun dumpDescriptors(conn: UsbDeviceConnection, intf: UsbInterface) {
        // Raw USB descriptors (device + config) as seen by the host.
        val raw = conn.rawDescriptors
        if (raw != null) logHex("raw USB descriptors", raw, raw.size)

        // HID report descriptor: GET_DESCRIPTOR(type=0x22) on the interface.
        val buf = ByteArray(4096)
        val n = conn.controlTransfer(0x81, 0x06, 0x22 shl 8, intf.id, buf, buf.size, 2000)
        if (n > 0) logHex("HID report descriptor (intf ${intf.id})", buf, n)
        else Log.w(TAG, "HID report descriptor fetch failed ($n)")
    }

    private fun logHex(label: String, b: ByteArray, len: Int) {
        Log.i(TAG, "$label: $len bytes")
        var i = 0
        while (i < len) {
            val end = (i + 32).coerceAtMost(len)
            val sb = StringBuilder()
            for (j in i until end) sb.append("%02x ".format(b[j]))
            Log.i(TAG, "  [%04d] %s".format(i, sb.toString().trim()))
            i = end
        }
    }

    fun stop() {
        running = false
        thread?.join(500)
        thread = null
        try { iface?.let { connection?.releaseInterface(it) } } catch (_: Exception) {}
        try { connection?.close() } catch (_: Exception) {}
        connection = null
        iface = null
    }

    private fun findInterface(device: UsbDevice): UsbInterface? {
        for (i in 0 until device.interfaceCount) {
            val intf = device.getInterface(i)
            if (intf.id == IMU_INTERFACE_ID &&
                intf.interfaceClass == UsbConstants.USB_CLASS_HID) {
                return intf
            }
        }
        // fall back to the first interface with the target id, any class
        for (i in 0 until device.interfaceCount) {
            if (device.getInterface(i).id == IMU_INTERFACE_ID) return device.getInterface(i)
        }
        return null
    }

    private fun findEndpoint(intf: UsbInterface, direction: Int): UsbEndpoint? {
        for (i in 0 until intf.endpointCount) {
            val ep = intf.getEndpoint(i)
            if (ep.direction == direction) return ep
        }
        return null
    }

    private fun readLoop(conn: UsbDeviceConnection, inEp: UsbEndpoint, outEp: UsbEndpoint?) {
        val buf = ByteArray(inEp.maxPacketSize.coerceAtLeast(64))
        var packetCount = 0
        var emptyStreak = 0
        Log.i(TAG, "Read loop started (bufSize=${buf.size}); listening for unsolicited data...")

        while (running) {
            val n = conn.bulkTransfer(inEp, buf, buf.size, READ_TIMEOUT_MS)
            if (n > 0) {
                emptyStreak = 0
                packetCount++
                if (packetCount <= 60 || packetCount % 200 == 0) {
                    Log.i(TAG, "pkt #$packetCount len=$n : ${hex(buf, n)}")
                }
            } else {
                emptyStreak++
                // Interrupt IN is silent: fall back to polling GET_REPORT, which
                // returned data during enable. Log samples to see if they are
                // live IMU values (they should change as the glasses move).
                if (emptyStreak == 2 && outEp != null) {
                    Log.w(TAG, "No unsolicited data; polling GET_REPORT instead")
                }
                pollReport(conn)
            }
        }
        Log.i(TAG, "Read loop stopped after $packetCount packets")
    }

    private var pollCount = 0
    private val pollBuf = ByteArray(1024)
    private fun pollReport(conn: UsbDeviceConnection) {
        val n = conn.controlTransfer(0xA1, 0x01, 0x0100, IMU_INTERFACE_ID, pollBuf, pollBuf.size, 500)
        if (n > 0) {
            pollCount++
            if (pollCount <= 40 || pollCount % 100 == 0) {
                Log.i(TAG, "poll #$pollCount len=$n : ${hex(pollBuf, n)}")
            }
        }
    }

    private fun hex(b: ByteArray, len: Int): String {
        val sb = StringBuilder(len * 3)
        val show = len.coerceAtMost(48)
        for (i in 0 until show) sb.append("%02x ".format(b[i]))
        if (len > show) sb.append("...")
        return sb.toString().trim()
    }
}
