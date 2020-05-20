package ca.thotmail.fahcontrol

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.thotmail.fahcontrol.storage.*
import kotlinx.android.synthetic.main.fragment_mini_connection.view.*

class MainAdapter: RecyclerView.Adapter<CustomViewHolder> {

    private var dao : ConnInfoDao
    private lateinit var connections : List<ConnectionInfo>

    constructor(dao: ConnInfoDao){
        this.dao = dao
        updateConnections()
    }

    fun updateConnections(){
        connections = dao.getAll()
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.fragment_mini_connection, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.IPAddressPort.text = "192.168.1.249:111"
    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.setOnClickListener {
            val intent = Intent(view.context, DetailConnectionActivity::class.java)

            view.context.startActivity(intent)
        }
    }
}