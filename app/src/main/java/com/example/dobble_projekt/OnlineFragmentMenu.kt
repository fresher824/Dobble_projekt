package com.example.dobble_projekt

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.dobble_projekt.databinding.FragmentOnlineMenuBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class OnlineFragmentMenu : Fragment() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: FragmentOnlineMenuBinding
    private val SELECT_DEVICE = 102
    private var gameUtils: GameUtils? = null

    companion object {
        private const val TAG = "MY_APP_DEBUG_TAG"

        const val MESSAGE_STATE_CHANGED: Int = 0
        const val MESSAGE_READ = 1
        const val MESSAGE_WRITE = 2
        const val MESSAGE_DEVICE_NAME = 3
        const val MESSAGE_TOAST = 4

        const val DEVICE_NAME = "deviceName"
        const val TOAST = "toast"
        private var connectedDevice: String = null.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnlineMenuBinding.inflate(inflater, container, false)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        gameUtils = GameUtils(context, handler)
        if (bluetoothAdapter.isEnabled)
        {
            val img: ImageView = binding.bluetoothView
            img.setImageResource(R.drawable.bluetooth_on)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            val address = data?.getStringExtra("deviceAddress")
            gameUtils?.connect(bluetoothAdapter.getRemoteDevice(address))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bluetoothView.setOnClickListener {
            updateBTImage()
        }
        binding.createButton.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                val intent: Intent = Intent(activity, chooseDevice::class.java)
                startActivityForResult(intent, SELECT_DEVICE)
                //findNavController().navigate(R.id.action_onlineFragmentMenu_to_deviceListFragment)
            }
            else
                Snackbar.make(binding.root, "Turn on bluetooth!", Snackbar.LENGTH_LONG).show()
        }
        binding.joinButton.setOnClickListener {
            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
            startActivity(discoverableIntent)
            Toast.makeText(context, "Your device is now discoverable for 300 seconds", Toast.LENGTH_SHORT).show()
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

    private val handler = Handler { message ->
        when (message.what) {
            MESSAGE_STATE_CHANGED -> when (message.arg1) {
                /*ChatUtils.STATE_NONE -> setState("Not Connected")
                ChatUtils.STATE_LISTEN -> setState("Not Connected")
                ChatUtils.STATE_CONNECTING -> setState("Connecting...")
                ChatUtils.STATE_CONNECTED -> setState("Connected: $connectedDevice")*/
            }
            MESSAGE_WRITE -> {
                val buffer1 = message.obj as ByteArray
                //val outputBuffer = String(buffer1)
            }
            MESSAGE_READ -> {
                val buffer = message.obj as ByteArray
                //val inputBuffer = String(buffer, 0, message.arg1)
            }
            MESSAGE_DEVICE_NAME -> {
               connectedDevice =
                   message.data.getString(DEVICE_NAME)!!
                Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show()
            }
            MESSAGE_TOAST -> Toast.makeText(
                context,
                message.data.getString(TOAST),
                Toast.LENGTH_SHORT
            ).show()
        }
        false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (gameUtils != null)
            gameUtils?.stop()
    }


}

