package ca.thotmail.fahcontrol

import android.util.Log.d
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

fun isOnline(info: ConnectionInfo): Boolean {
    d("Debug", info.ipAddr)
    try {
        val c = Socket()
        c.connect(InetSocketAddress(info.ipAddr, info.port), 2*1000)
        if (c.isConnected) {
            d("Debug", "connected")
            val transmission = Scanner(c.getInputStream()).nextLine().substring(7) //it starts with some non printable characters
            d("Debug", transmission)
            if (transmission == "Welcome to the Folding@home Client command server.") {
                return true
            }
        }
    }
    catch (e:Exception){
        d("Debug", "exception")
        d("Debug", e.toString())
        return false
    }
    return false
}

class FAHConnection {
    private val info : ConnectionInfo

    constructor(info: ConnectionInfo){
        this.info = info
    }


}
