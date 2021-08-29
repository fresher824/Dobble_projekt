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
    val requestCode = 1;
    val MY_UUID: UUID = UUID.randomUUID()
    val NAME: String = "Dobble projekt"
    private var gameUtils: GameUtils? = null

    companion object {
        const val MESSAGE_CONNECTED: Int = 3
        const val MESSAGE_CONNECTION_FAIL: Int = 3
        private const val TAG = "MY_APP_DEBUG_TAG"

        // Defines several constants used when transmitting messages between the
        // service and the UI.
        const val MESSAGE_STATE_CHANGED: Int = 0
        const val MESSAGE_READ = 1
        const val MESSAGE_WRITE = 2
        const val MESSAGE_DEVICE_NAME = 3
        const val MESSAGE_TOAST = 4
        const val DEVICE_NAME = "deviceName"
        const val TOAST = "toast"
        // ... (Add other message types here as needed.)
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
            Toast.makeText(context, "Address: " + address, Toast.LENGTH_SHORT).show()
            ConnectThread(bluetoothAdapter.getRemoteDevice(address)).run()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            val address = data.getStringExtra("deviceAddress")
            //chatUtils.connect(bluetoothAdapter.getRemoteDevice(address))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/

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
            startActivityForResult(discoverableIntent, requestCode)
            Toast.makeText(context, "Your device is now discoverable for 300 seconds", Toast.LENGTH_SHORT).show()
            AcceptThread()
            AcceptThread().run()
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
                /*val buffer1 = message.obj as ByteArray
                val outputBuffer = String(buffer1)
                adapterMainChat.add("Me: $outputBuffer")*/
            }
            MESSAGE_READ -> {
                /*val buffer = message.obj as ByteArray
                val inputBuffer = String(buffer, 0, message.arg1)
                adapterMainChat.add(connectedDevice.toString() + ": " + inputBuffer)*/
            }
            MESSAGE_DEVICE_NAME -> {
               /* connectedDevice =
                    message.data.getString(DEVICE_NAME)
                Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show()*/
            }
            MESSAGE_TOAST -> Toast.makeText(
                context,
                message.data.getString(TOAST),
                Toast.LENGTH_SHORT
            ).show()
        }
        false
    }

    private inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    //manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

    }


    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    private fun manageConnectedSocket(socket: BluetoothSocket) {
        //val connectedThread: MyBluetoothService.ConnectedThread = MyBluetoothService.ConnectedThread(socket)
        //connectedThread.start()
    }

    class MyBluetoothService(
        // handler that gets info from Bluetooth service
        private val handler: Handler
    ) {

        inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

            private val mmInStream: InputStream = mmSocket.inputStream
            private val mmOutStream: OutputStream = mmSocket.outputStream
            private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

            override fun run() {
                var numBytes: Int // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    // Read from the InputStream.
                    numBytes = try {
                        mmInStream.read(mmBuffer)
                    } catch (e: IOException) {
                        Log.d(TAG, "Input stream was disconnected", e)
                        break
                    }

                    // Send the obtained bytes to the UI activity.
                    val readMsg = handler.obtainMessage(
                        MESSAGE_READ, numBytes, -1,
                        mmBuffer)
                    readMsg.sendToTarget()
                }
            }

            // Call this from the main activity to send data to the remote device.
            fun write(bytes: ByteArray) {
                try {
                    mmOutStream.write(bytes)
                } catch (e: IOException) {
                    Log.e(TAG, "Error occurred when sending data", e)

                    // Send a failure message back to the activity.
                    val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                    val bundle = Bundle().apply {
                        putString("toast", "Couldn't send data to the other device")
                    }
                    writeErrorMsg.data = bundle
                    handler.sendMessage(writeErrorMsg)
                    return
                }

                // Share the sent message with the UI activity.
                val writtenMsg = handler.obtainMessage(
                    MESSAGE_WRITE, -1, -1, mmBuffer)
                writtenMsg.sendToTarget()
            }

            // Call this method from the main activity to shut down the connection.
            fun cancel() {
                try {
                    mmSocket.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Could not close the connect socket", e)
                }
            }
        }
    }


}

