package ca.thotmail.fahcontrol


import android.util.Log.d
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.regex.Pattern

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
        //read consts
        val PYON_START_PATTERN: Pattern = Pattern.compile("PyON \\d+ \\w+")
        const val PYON_END_STRING = "---"//this actually ends with a \n however we are using readline which removes them

        //write consts
        const val PROMPT_STRING = "> "
        const val AUTH_STRING = "auth %s\n"
        const val FOLD_STRING = "unpause\n"
        const val FINISH_STRING = "finish\n"
        const val PAUSE_STRING = "pause\n"
        const val HEART_STRING = "heartbeat\n"
        const val SLOT_INFO_STRING = "slot-info\n"
        const val EXIT_STRING = "exit\n"
        const val UPDATES_STRING = "updates clear\nupdates add 0 1 \$slot-info\n"
    }

    private val socket = Socket()
    private lateinit var parent : DetailConnectionActivity

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    var online = false
        private set
    private val aliveM = Mutex()

    //AUTO READS
    private lateinit var slots : JSONArray



    //AUTO READ MUTEXs
    private val slotsM = Mutex()

//    constructor(){
//
//    }


    suspend fun tryConnect(info: ConnectionInfo){
        try {
            socket.connect(InetSocketAddress(info.ipAddr, info.port), 2 * 1000)
        }
        catch (e:Exception){
            d("Exception", "exception in tryConnect")
            d("Exception", e.toString())
        }
        if(socket.isConnected){
            reader = socket.getInputStream().bufferedReader()
            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

            var trans = readLine().substring(7)
            if (trans == "Welcome to the Folding@home Client command server.") {
                online = true
                if (info.paswd.isNotEmpty()) {
                    writer.write(String.format(AUTH_STRING, info.paswd))
                    writer.flush()
                    d("Debug", "auth sent")
                    trans = readLine()
                    d("Debug", "auth $trans")
                    if(!trans.contains("OK")){
                        readLineUntill(PYON_END_STRING) //clearing the failed message
                        online = false //TODO: maybe some type of passwd flag
                        //for now tho if user got into detail view then the server is up and the only possible explanation is bad password
                    }
                }
                else{
                    send(HEART_STRING)
                    val res = readLineUntill(PROMPT_STRING)
                    if(res.startsWith("ERROR")){
                        online = false//TODO: maybe some type of passwd flag
                    }
                }

            }
        }
        if(online){
            send(UPDATES_STRING)
            GlobalScope.launch(Dispatchers.IO) {
                keepReading()
            }
        }
        else{
            parent.updateSlots()
        }
    }

    fun setParent(p:DetailConnectionActivity){
        parent = p
    }


    //REQUIRES A DEDICATED THREAD
    private suspend fun keepReading(){
        while(online){

            val line = readLine()
            //d("Debug", "itt redy ${reader.lineSequence().iterator().hasNext()}")
            if (PYON_START_PATTERN.matcher(line).matches()) {
                //various thingamabobs
                if (line.endsWith("slots")) {
                    val temp = JSONArray(readLineUntill(PYON_END_STRING))
                    slotsM.withLock {
                        slots = temp
                    }
                    parent.updateSlots()
                } else if (line.endsWith("units")) {
                    //TODO: same as above but for units
                }
            }
        }
        d("Debug", "loop reader ended")
    }

    private suspend fun readLine():String{
        try {
            var trans: String
            do {
                trans = reader.readLine()
            } while (trans == PROMPT_STRING)
            return trans
        }
        catch (e:Exception){
            d("Exception", "exception in readLine, expected on exiting detailed view")
            d("Exception", e.toString())
            online = false
            return ""
        }
    }

    private suspend fun readLineUntill(waitTill:String):String{
        try {
            var fin = StringBuilder()
            var trans = ""
            do {
                fin.append(trans)
                trans = reader.readLine()
            } while (trans != waitTill)
            return fin.toString()
        }
        catch (e:Exception){
            d("Exception", "exception in readLineUntill, expected on exiting detailed view")
            d("Exception", e.toString())
            online = false
            return ""
        }
    }

    suspend fun close(){
        send(EXIT_STRING)
        online = false
        reader.close()
        writer.close()
        socket.close()
        d("Debug", "Conection closed")
    }



    private suspend fun send(msg:String){
        if(online) {
            writer.write(msg)
            writer.flush()
        }
    }

    suspend fun sendFold(){
        send(FOLD_STRING)
        sendSlotInfo()
    }

    suspend fun sendFinish(){
        send(FINISH_STRING)
        sendSlotInfo()
    }

    suspend fun sendPause(){
        send(PAUSE_STRING)
        sendSlotInfo()
    }

    suspend fun sendSlotInfo(){
        send(SLOT_INFO_STRING)
    }



    suspend fun getSlots():JSONArray?{
        var temp : JSONArray? = null
        if(this::slots.isInitialized) {
            slotsM.withLock {
                temp = slots
            }
        }
        return temp
    }


}
