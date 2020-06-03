package ca.thotmail.fahcontrol

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import kotlinx.android.synthetic.main.activity_detail_connection.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailConnectionActivity : AppCompatActivity() {

    private var con  = FAHConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_connection)

        val info = intent.getSerializableExtra("info") as ConnectionInfo

        GlobalScope.launch(Dispatchers.IO){
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
}