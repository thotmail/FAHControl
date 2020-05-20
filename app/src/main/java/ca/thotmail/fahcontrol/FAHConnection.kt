package ca.thotmail.fahcontrol

import ca.thotmail.fahcontrol.storage.ConnectionInfo
import java.lang.Exception
import java.net.Socket
import java.util.*

fun isOnline(info: ConnectionInfo): Boolean {
    try {
        val c = Socket(info.ipAddr, info.port)
        if (c.isConnected) {
            val transmission = Scanner(c.getInputStream()).nextLine()
            if (transmission.equals(R.string.expected_first_recieve)) {
                return true
            }
        }
    }
    catch (e:Exception){
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
