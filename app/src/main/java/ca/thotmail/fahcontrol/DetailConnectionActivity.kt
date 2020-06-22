package ca.thotmail.fahcontrol

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import kotlinx.android.synthetic.main.activity_detail_connection.*
import kotlinx.coroutines.*

class DetailConnectionActivity : AppCompatActivity() {

    private var con  = FAHConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_connection)

        val info = intent.getSerializableExtra("info") as ConnectionInfo

        GlobalScope.launch(Dispatchers.IO){
            con.setParent(this@DetailConnectionActivity)
            con.tryConnect(info)
        }


        supportActionBar?.title = info.nickname

        button_fold.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                con.sendFold()
            }
        }

        button_pause.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                con.sendPause()
            }
        }

        button_finish.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                con.sendFinish()
            }
        }



    }

    fun updateSlots(){
        if(con.online) {
            GlobalScope.launch(Dispatchers.IO) {
                var slot = con.getSlots()

                GlobalScope.launch(Dispatchers.Main) {
                    slot_status.text = slot!!.getJSONObject(0).getString("status")
                    if (slot_status.text == "FINISHING" || slot_status.text == "STOPPING" || slot_status.text == "PAUSED") {
                        slot_status.setBackgroundColor(Color.YELLOW)
                    } else if (slot_status.text == "RUNNING") {
                        slot_status.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }
        else{
            GlobalScope.launch(Dispatchers.Main) {
                slot_status.text = "Probably Bad Password"
                slot_status.setBackgroundColor(Color.RED)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch(Dispatchers.IO) {
            con.close()
        }
    }
}