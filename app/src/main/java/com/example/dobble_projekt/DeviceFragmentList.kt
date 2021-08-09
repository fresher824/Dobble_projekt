package com.example.dobble_projekt

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dobble_projekt.databinding.FragmentDeviceListBinding
import android.bluetooth.BluetoothAdapter as BluetoothAdapter1

class DeviceFragmentList : Fragment() {

    private lateinit var binding: FragmentDeviceListBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()

        init()

        return binding.root

    }

    private fun init() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
        }
    }

}