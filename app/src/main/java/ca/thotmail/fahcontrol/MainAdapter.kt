package ca.thotmail.fahcontrol

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.thotmail.fahcontrol.storage.*
import kotlinx.android.synthetic.main.fragment_mini_connection.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainAdapter: RecyclerView.Adapter<CustomViewHolder> {

    private var dao : ConnInfoDao
    private lateinit var connections : List<ConnectionInfo>

    constructor(dao: ConnInfoDao){
        this.dao = dao
        updateConnections()
    }

    fun updateConnections(){
        GlobalScope.launch(Dispatchers.Main) {
            connections = dao.getAll()
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return if(this::connections.isInitialized) {
            connections.size
        }else{
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.fragment_mini_connection, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cur = connections[position]
        val combi = cur.ipAddr+":"+cur.port
        holder.info = cur

        holder.view.IPAddressPort.text = combi
        holder.view.Name.text = cur.nickname
        holder.view.Status.text = "Unknown"

        holder.update()

    }

}

class CustomViewHolder(val view: View, var info: ConnectionInfo? = null): RecyclerView.ViewHolder(view){
    init {
        view.setOnClickListener {
            if(view.Status.text == "Online") {
                val intent = Intent(view.context, DetailConnectionActivity::class.java)
                intent.putExtra("info", info)
                view.context.startActivity(intent)
            }
            else{
                update()
            }
        }
        view.setOnLongClickListener {

            val intent = Intent(view.context, DetailConnectionActivity::class.java)//TODO: go to edit instead
            intent.putExtra("info", info)
            view.context.startActivity(intent)

            true
        }
    }
    fun update(){
        view.Status.text = "Checking"
        view.Status.setBackgroundColor(Color.YELLOW)
        GlobalScope.launch(Dispatchers.Default){
            if(isOnline(info!!)){
                view.Status.text = "Online"
                view.Status.setBackgroundColor(Color.GREEN)
            }
            else{
                view.Status.text = "Offline"
                view.Status.setBackgroundColor(Color.RED)
            }
        }
    }
}