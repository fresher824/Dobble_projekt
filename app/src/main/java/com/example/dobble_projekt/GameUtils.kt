package com.example.dobble_projekt

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Handler
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket

import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Message
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*






class GameUtils(c: Context?, h: Handler) {
    private var context: Context? = c
    private var handler: Handler? = h

    val MY_UUID: UUID = UUID.randomUUID()
    val NAME: String = "Dobble projekt"

    private var connectThread: ConnectThread? = null
    private var acceptThread: AcceptThread? = null
    private var connectedThread: ConnectedThread? = null

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val STATE_NONE = 0
    val STATE_LISTEN = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3

    private var state = STATE_NONE

    fun gState(): Int {
        return state
    }

    @Synchronized fun setState(s: Int){
        this.state = s
        handler?.obtainMessage(OnlineFragmentMenu.MESSAGE_STATE_CHANGED, state, -1)?.sendToTarget()
    }

    @Synchronized fun start(){
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }

        if (acceptThread == null) {
            acceptThread = AcceptThread()
            acceptThread!!.start()
        }

        if (connectedThread != null)
        {
            connectedThread!!.cancel()
            connectedThread = null
        }

        setState(STATE_LISTEN)
    }

    @Synchronized fun stop(){
        if (connectThread != null) {
            connectThread!!.cancel();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread!!.cancel();
            acceptThread = null;
        }

        if (connectedThread != null)
        {
            connectedThread!!.cancel()
            connectedThread = null
        }

        setState(STATE_NONE)
    }

    fun connect(device: BluetoothDevice?){
        if (state == STATE_CONNECTED)
        {
            connectThread?.cancel()
            connectThread = null
        }

        connectThread = ConnectThread(device)
        connectThread?.start()

        setState(STATE_CONNECTING)
    }


    fun write(buffer: ByteArray?) {
        var connThread: ConnectedThread
        synchronized(this) {
            if (state != STATE_CONNECTED) {
                return
            }
            connThread = connectedThread!!
        }
        connThread.write(buffer!!)
    }

    private inner class AcceptThread : Thread(){
        private var serverSocket: BluetoothServerSocket? = try{
            bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID)
        } catch (e: IOException){
            Log.e(TAG,"Accept->Constructor", e)
            null
        }

        override fun run(){
            var socket: BluetoothSocket? = null
            while (true) {
                try {
                    socket = serverSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Accept->Run", e)
                    try {
                        serverSocket?.close()
                    } catch (e1: IOException) {
                        Log.e(TAG, "Accept->Close", e1)
                    }
                }

                if (socket != null) {
                    when (gState()) {
                        STATE_LISTEN, STATE_CONNECTING -> connected(socket, socket.remoteDevice)
                        STATE_NONE, STATE_CONNECTED -> {
                            try {
                                socket.close()
                            } catch (e: IOException) {
                                Log.e(TAG, "Accept->CloseSocket", e)
                            }
                        }
                    }
                }
            }
        }

        fun cancel() {
            try {
                serverSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG,"Accept->CloseServer", e)
            }
        }
    }

    private inner class ConnectThread(d: BluetoothDevice?) : Thread(){
        private var device: BluetoothDevice? = d

        private var socket: BluetoothSocket? = try {
            device?.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (e: IOException) {
            Log.e(TAG,"Connect->Constructor", e)
            null
        }

        override fun run(){
            try {
                socket?.connect()
            } catch (e: IOException){
                try {
                    socket?.close()
                } catch (e1: IOException){
                    Log.e(TAG,"Connect->CloseSocket")
                }
                Log.e(TAG,"Connect->CloseSocket")
                connectionFailed()
                return

            }

        @Synchronized
        connectThread = null

        connected(socket, device)
        }

        fun cancel(){
            try {
                socket?.close()
            } catch (e: IOException){
                Log.e(TAG,"Connect->Cancel", e)
            }
        }

    }


    private inner class ConnectedThread(soc: BluetoothSocket?) : Thread(){
        private var socket: BluetoothSocket? = soc
        private val inputStream: InputStream? = try {
            socket!!.inputStream
        } catch (e: IOException){
            null
        }
        private val outputStream: OutputStream? = try {
            socket!!.outputStream
        } catch (e: IOException){
            null
        }

        override fun run(){
            val buffer = ByteArray(1024)
            val bytes: Int

            try {
                bytes = inputStream!!.read(buffer)
                handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_READ, bytes, -1, buffer).sendToTarget()
            } catch (e: IOException) {
                connectionLost()
            }
        }

        private fun connectionLost() {
            val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_TOAST)
            val bundle: Bundle = Bundle()
            bundle.putString(OnlineFragmentMenu.TOAST, "Connection lost")
            message.data = bundle
            handler?.sendMessage(message)

            this@GameUtils.start()
        }

        fun write(buffer: ByteArray){
            try {
                outputStream?.write(buffer)
                handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_WRITE, -1, -1, buffer).sendToTarget()
            } catch (e: IOException) {
            }
        }

        fun cancel() {
            try {
                socket!!.close()
            } catch (e: IOException) {
            }
        }

    }


    @Synchronized
    private fun connectionFailed() {
        val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_TOAST)
        val bundle: Bundle = Bundle()
        bundle.putString(OnlineFragmentMenu.TOAST, "Cannot connect to the device. ")
        message.data = bundle
        handler!!.sendMessage(message)

        this.start()
    }

    @Synchronized
    private fun connected(socket: BluetoothSocket?, device: BluetoothDevice?) {
        if (connectThread != null)
        {
            connectThread?.cancel()
            connectThread = null
        }

        if (connectedThread != null)
        {
            connectedThread!!.cancel()
            connectedThread = null
        }

        connectedThread = ConnectedThread(socket);
        connectedThread!!.start();

        val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_DEVICE_NAME)
        val bundle: Bundle = Bundle()
        bundle.putString(OnlineFragmentMenu.DEVICE_NAME, device?.name)
        message.data = bundle
        handler!!.sendMessage(message)

        setState(STATE_CONNECTED)
    }
}