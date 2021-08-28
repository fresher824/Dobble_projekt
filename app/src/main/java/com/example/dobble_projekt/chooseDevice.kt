package com.example.dobble_projekt

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.example.dobble_projekt.databinding.ActivityChooseDeviceBinding
import com.example.dobble_projekt.databinding.ActivityMainBinding
import android.widget.TextView

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener


class chooseDevice : AppCompatActivity() {

    private lateinit var binding: ActivityChooseDeviceBinding
    lateinit var listPairedDevices: ListView
    lateinit var listAvailableDevices: ListView
    lateinit var adapterPairedDevices: ArrayAdapter<String>
    lateinit var adapterAvailableDevices: ArrayAdapter<String>
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var progressScanDevices: ProgressBar
    companion object {
        // Return Intent extra
        var EXTRA_DEVICE_ADDRESS = "device_address"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, filter1)

        binding.button3.setOnClickListener { scanDevices() }

        init()
    }

    private fun init()
    {
        listPairedDevices = binding.listPairedDevices
        listAvailableDevices = binding.listAvailableDevices
        progressScanDevices = binding.progressScanDevices

        adapterPairedDevices = ArrayAdapter<String>(this, R.layout.device_list_item)
        adapterAvailableDevices = ArrayAdapter<String>(this, R.layout.device_list_item)

        listPairedDevices.adapter = adapterPairedDevices
        listAvailableDevices.adapter = adapterAvailableDevices

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        if (pairedDevices != null && pairedDevices.size > 0)
        {
            for (device in pairedDevices)
            {
                adapterPairedDevices.add(device.name + "\n" + device.address)
            }
        }

        listAvailableDevices.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                val info = (view as TextView).text.toString()
                val address = info.substring(info.length - 17)
                val intent = Intent()
                intent.putExtra("deviceAddress", address)
                setResult(RESULT_OK, intent)
                finish()
            }

    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action)
            {
                BluetoothDevice.ACTION_FOUND ->
                {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device!!.bondState != BluetoothDevice.BOND_BONDED) {
                        adapterAvailableDevices.add(device.name + "\n" + device.address)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->
                {
                    progressScanDevices.visibility = GONE
                    if (adapterAvailableDevices.count == 0)
                    {
                        Toast.makeText(this@chooseDevice, "No new devices found", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@chooseDevice, "Click on the device to start game", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        }

    private  fun scanDevices()
    {
        progressScanDevices.visibility = VISIBLE
        adapterAvailableDevices.clear()
        Toast.makeText(this, "Scan started", Toast.LENGTH_SHORT).show()

        if (bluetoothAdapter.isDiscovering)
        {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        this.unregisterReceiver(receiver)
    }

}