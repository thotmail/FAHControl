package ca.thotmail.fahcontrol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.thotmail.fahcontrol.storage.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val REQ_CODE_ADD_SERVER = 1
const val REQ_CODE_EDIT_SERVER = 2

class MainActivity : AppCompatActivity() {



    private lateinit var dao : ConnInfoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = DB.getInstance(applicationContext)
        dao = db.ConnInfoDao()

        MainRecycler.layoutManager = LinearLayoutManager(this)
        MainRecycler.adapter = MainAdapter(this, dao)

        addConnection.setOnClickListener {
            startActivityForResult(Intent(this, AddConnectionActivity::class.java), REQ_CODE_ADD_SERVER)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQ_CODE_ADD_SERVER -> addServerResult(resultCode, data)
            REQ_CODE_EDIT_SERVER -> editServerResult(resultCode, data)
        }
    }

    private fun editServerResult(resultCode: Int, data: Intent?){
        if(resultCode == Activity.RESULT_OK){
            if(data!!.hasExtra("info")){
                //TODO: edit the toEdit and make it info
            }
            else{
                //TODO: delete entry that matches toEdit
            }
        }
    }

    private fun addServerResult(resultCode: Int, data: Intent?){
        if(resultCode == Activity.RESULT_OK) {

            val info = data?.getSerializableExtra("info") as ConnectionInfo
            GlobalScope.launch {
                dao.insertAll(info)

                (MainRecycler.adapter as MainAdapter).updateConnections()
            }
        }
    }

}
