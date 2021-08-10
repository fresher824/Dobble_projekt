package com.example.dobble_projekt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dobble_projekt.databinding.FragmentDeviceListBinding
import android.R




/**
 * A fragment representing a list of Items.
 */
class DeviceListFragment : Fragment() {

    lateinit var binding: FragmentDeviceListBinding
    lateinit var bluetoothAdapter: BluetoothAdapter

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

        if (binding.pairedDevList is RecyclerView) {
            with(binding.pairedDevList) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyDeviceListRecyclerViewAdapter(names, addresses)
            }
        }
    }

}