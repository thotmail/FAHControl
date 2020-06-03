package ca.thotmail.fahcontrol

import android.util.Log.d
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

fun isOnline(info: ConnectionInfo): Boolean {
    try {
        val c = Socket()
        c.connect(InetSocketAddress(info.ipAddr, info.port), 2*1000)
        if (c.isConnected) {
            val transmission = Scanner(c.getInputStream()).nextLine().substring(7) //it starts with some non printable characters
            if (transmission == "Welcome to the Folding@home Client command server.") {
                c.close()
                return true
            }
        }
    }
    catch (e:Exception){
        d("Exception", "exception in isOnline")
        d("Exception", e.toString())
        return false
    }
    return false
}

@Suppress("BlockingMethodInNonBlockingContext")

class FAHConnection {

    private companion object{
        const val AUTH_STRING = "auth %s\n"
        const val FOLD_STRING = "unpause\n"
        const val FINISH_STRING = "finish\n"
        const val PAUSE_STRING = "pause\n"
    }

    private val socket = Socket()
    lateinit var reader: BufferedReader
        private set
    lateinit var writer: BufferedWriter
        private set
    var online = false
        private set

    constructor(){

    }


    suspend fun tryConnect(info: ConnectionInfo){
        try {
            socket.connect(InetSocketAddress(info.ipAddr, info.port), 2 * 1000)
        }
        catch (e:Exception){
            d("Exception", "exception in tryConnect")
            d("Exception", e.toString())
        }
        if(socket.isConnected){
            reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            var trans = readLine().substring(7)
            if (trans == "Welcome to the Folding@home Client command server.") {
                online = true
                if (info.paswd.isNotEmpty()) {
                    writer.write(String.format(AUTH_STRING, info.paswd))
                    writer.flush()
                    d("Debug", "auth sent, reading")
                    trans = readLine()
                    d("Debug", "transmission: $trans")
                    if(!trans.contains("OK")){
                        readLineUntill("---") //multiple lines are returned upon auth fail
                        online = false //TODO: maybe a bad passwd flag
                    }
                }
            }
        }
    }

    private suspend fun readLine():String{
        var trans:String
        do{
            trans = reader.readLine()
        }
        while(trans == "> ")
        return trans
    }

    private suspend fun readLineUntill(waitTill:String):String{
        var trans:String
        do{
            trans = reader.readLine()
        }
        while(trans == waitTill)
        return trans
    }

    suspend fun close(){
        reader.close()
        writer.close()
        socket.close()
    }

    suspend fun sendFold(){
        if(online) {
            writer.write(FOLD_STRING)
            writer.flush()
        }
    }

    suspend fun sendFinish(){
        if(online) {
            writer.write(FINISH_STRING)
            writer.flush()
        }
    }

    suspend fun sendPause(){
        if(online) {
            writer.write(PAUSE_STRING)
            writer.flush()
        }
    }


}
