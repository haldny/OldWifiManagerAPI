package com.haldny.oldwifimanagerapi

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.recycle_content_main.view.*

class MyAdapter(private var networks: MutableList<Network>,
                private val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    fun updateNetworks(newNetworks: MutableList<Network>) {
        networks = newNetworks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycle_content_main, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("HSS", "Bind View Holder: $position, ${networks[position]}")
        val network = networks[position]
        holder.bindView(network, position)
    }

    override fun getItemCount(): Int {
        Log.d("HSS", "Size: ${networks.size}")
        return networks.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val id: TextView = itemView.tv_db_id
        private val ssid: TextView = itemView.tv_db_ssid
        private val remove: ImageView = itemView.bt_remove
        private val connect: ImageView = itemView.bt_connect

        fun bindView(network: Network, position: Int) {
            id.text = network.id.toString()
            ssid.text = network.ssid

            remove.setOnClickListener {
                Log.d("HSS", "Remove Network: $network")

                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.removeNetwork(network.id)
                networks.remove(network)
                notifyItemRemoved(position)

                Snackbar.make(it, "Removed Network: $network", Snackbar.LENGTH_LONG).show()
            }

            connect.setOnClickListener {
                Log.d("HSS", "Connect to network: $network")

                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val result = wifiManager.enableNetwork(network.id, true)

                Log.d("HSS", "Network was enabled: $result")
            }
        }

    }

}