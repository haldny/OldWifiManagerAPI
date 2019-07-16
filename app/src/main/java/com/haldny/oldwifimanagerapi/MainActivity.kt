package com.haldny.oldwifimanagerapi

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import java.util.stream.Collectors
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog

import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(getNetworks(), this)

        fab.setOnClickListener { view ->
            openDialog(view)
        }

        disconnect.setOnClickListener {
            val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.disconnect()
        }

        recycle_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDialog(view: View) {
        val layoutInflaterAndroid = LayoutInflater.from(view.context)
        val mView = layoutInflaterAndroid.inflate(R.layout.ssid_input_dialog, null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(view.context)
        alertDialogBuilderUserInput.setView(mView)

        val ssid = mView.findViewById(R.id.ssid) as EditText
        val password = mView.findViewById(R.id.password) as EditText

        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_save)) { _, _ ->
                addNetwork(ssid, password)
            }
            .setNegativeButton(getString(R.string.dialog_cancel)) { dialogBox, _ ->
                dialogBox.cancel()
            }

        val alertDialogAndroid = alertDialogBuilderUserInput.create()
        alertDialogAndroid.show()
    }

    private fun addNetwork(ssid: EditText, password: EditText) {
        val ssidText = ssid.text.toString()
        val passwordText = password.text.toString()

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = quoteString(ssidText)
        wifiConfig.preSharedKey = quoteString(passwordText)

        Log.d("HSS", "Add network: ${wifiConfig.SSID}")
        val result = wifiManager.addNetwork(wifiConfig)
        Log.d("HSS", "Network was added: ${result != -1}")

        viewAdapter.updateNetworks(getNetworks())
        viewAdapter.notifyDataSetChanged()
    }

    private fun addNetworks(): Boolean {

        try {
            val list = mutableListOf(
                Network(-1, quoteString("SSID1")),
                Network(-1, quoteString("SSID2")),
                Network(-1, quoteString("SSID3")),
                Network(-1, quoteString("SSID4")),
                Network(-1, quoteString("SSID5"))
            )

            Log.d("HSS", "create list of networks to be added")

            val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

            for (network in list) {
                val wifiConfig = WifiConfiguration()
                wifiConfig.SSID = network.ssid.toString()

                Log.d("HSS", "Add network: ${wifiConfig.SSID}")
                val result = wifiManager.addNetwork(wifiConfig)
                Log.d("HSS", "Network was added: ${result != -1}")
            }

            return true
        } catch (e: Exception) {
            Log.d("HSS", "Error when we are adding the networks: ${e.message}.")
            return false
        }

    }

    private fun getNetworks(): MutableList<Network> {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val listWifiConfigs = wifiManager.configuredNetworks

        return listWifiConfigs.stream()
            .map { wifiConfig -> Network(wifiConfig.networkId, wifiConfig.SSID) }
            .collect(Collectors.toList())
    }

    private fun quoteString(text: String) = String.format("\"%s\"", text)

}
