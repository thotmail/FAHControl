package ca.thotmail.fahcontrol

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.thotmail.fahcontrol.storage.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQ_CODE_ADD_SERVER = 1

    private lateinit var dao : ConnInfoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = DB.getInstance(applicationContext)
        dao = db.ConnInfoDao()

        MainRecycler.layoutManager = LinearLayoutManager(this)
        MainRecycler.adapter = MainAdapter(dao)

        addConnection.setOnClickListener {
            startActivityForResult(Intent(this, AddConnectionActivity::class.java), REQ_CODE_ADD_SERVER)
            (MainRecycler.adapter as MainAdapter).updateConnections()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQ_CODE_ADD_SERVER -> addServerResult(resultCode, data)
        }
    }

    private fun addServerResult(resultCode: Int, data: Intent?){

    }

}
