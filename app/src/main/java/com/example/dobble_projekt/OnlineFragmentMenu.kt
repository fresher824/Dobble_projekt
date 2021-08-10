package com.example.dobble_projekt

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.dobble_projekt.databinding.FragmentOnlineMenuBinding
import com.google.android.material.snackbar.Snackbar

class OnlineFragmentMenu : Fragment() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: FragmentOnlineMenuBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnlineMenuBinding.inflate(inflater, container, false)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled)
        {
            val img: ImageView = binding.bluetoothView
            img.setImageResource(R.drawable.bluetooth_on)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bluetoothView.setOnClickListener {
            updateBTImage()
        }
        binding.createButton.setOnClickListener {
            if (bluetoothAdapter.isEnabled)
                findNavController().navigate(R.id.action_onlineFragmentMenu_to_deviceListFragment)
            else
                Snackbar.make(binding.root, "Turn on bluetooth!", Snackbar.LENGTH_LONG).show()
        }
        binding.joinButton.setOnClickListener {

        }
    }

    private fun updateBTImage(){
        val img: ImageView = binding.bluetoothView
        if (bluetoothAdapter.isEnabled)
        {
            Snackbar.make(binding.root, "Bluetooth is already on", Snackbar.LENGTH_SHORT).show()
        }
        else
        {
            Snackbar.make(binding.root, "Bluetooth is now on", Snackbar.LENGTH_SHORT).show()
            img.setImageResource(R.drawable.bluetooth_on)
            bluetoothAdapter.enable()
        }
    }

}