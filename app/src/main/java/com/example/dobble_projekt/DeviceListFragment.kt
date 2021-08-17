package com.example.dobble_projekt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dobble_projekt.databinding.FragmentDeviceListBinding
import android.R
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager


/**
 * A fragment representing a list of Items.
 */
class DeviceListFragment : Fragment() {

    lateinit var binding: FragmentDeviceListBinding
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.requireActivity().registerReceiver(receiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceListBinding.inflate(layoutInflater)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the adapter
        val eventListener = this
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val names: MutableList<String> = ArrayList()
        val addresses: MutableList<String> = ArrayList()
        for (bt in pairedDevices!!)
        {
            names.add(bt.name)
            addresses.add(bt.address)
        }

        binding.button.setOnClickListener {
        if (bluetoothAdapter.isDiscovering)
            bluetoothAdapter.cancelDiscovery()

            bluetoothAdapter.startDiscovery()
        }

        if (binding.pairedDevList is RecyclerView) {
            with(binding.pairedDevList) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyDeviceListRecyclerViewAdapter(names, addresses)
            }
        }
        if (binding.availableDevList is RecyclerView) {
            with(binding.availableDevList) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyDeviceListRecyclerViewAdapter(avlnames, avladdresses)
            }
        }

    }

    val avlnames: MutableList<String> = ArrayList()
    val avladdresses: MutableList<String> = ArrayList()

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    avlnames.add(deviceName!!)
                    avladdresses.add(deviceHardwareAddress!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        this.requireActivity().unregisterReceiver(receiver)
    }

}