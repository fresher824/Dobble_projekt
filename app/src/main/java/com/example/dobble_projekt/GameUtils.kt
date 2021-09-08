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

    //Dane do połączenia BT
    val MY_UUID: UUID = UUID.fromString("ea2f5ed7-19f1-4156-b11b-865496134f82")
    val NAME: String = "Dobble projekt"

    //Zmienne do wywołania, akceptacji i wykonania połączenia
    private var connectThread: ConnectThread? = null
    private var acceptThread: AcceptThread? = null
    private var connectedThread: ConnectedThread? = null

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    //Stany połączenia
    val STATE_NONE = 0
    val STATE_LISTEN = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3

    private var state = STATE_NONE

    //Zwróc stan połączenia
    fun gState(): Int {
        return state
    }

    //Ustaw stan połączenia BT
    @Synchronized fun setState(s: Int){
        this.state = s
        handler?.obtainMessage(OnlineFragmentMenu.MESSAGE_STATE_CHANGED, state, -1)?.sendToTarget()
    }

    //Funkcja odpowiedzialna za wstęp do tworzenia połączenia
    //Wywolywana podczas rozpoczenia nasłuchiwania
    @Synchronized fun start(){
        //Jesli jest tworzone jakies polaczenie
        if (connectThread != null) {
            //Zakoncz je
            connectThread!!.cancel()
            connectThread = null
        }

        //Jesli nie ma akceptowanego zadnego polaczenia
        //1
        if (acceptThread == null) {
            //Rozpocznij mozliwosc akcetpacji połączenia
            acceptThread = AcceptThread()
            acceptThread!!.start()
        }

        //Jesli rozpoczeto przesyl danych
        if (connectedThread != null)
        {
            //Zakoncz go
            connectedThread!!.cancel()
            connectedThread = null
        }

        //Ustaw stan na nasluchiwanie
        setState(STATE_LISTEN)
    }

    //Funkcja odpowiedzialna za przerwanie akcji
    @Synchronized fun stop(){
        //Jesli jest tworzone jakies polaczenie
        if (connectThread != null) {
            //Zakoncz je
            connectThread!!.cancel()
            connectThread = null
        }
        //Jesli jest akceptowane jakies polaczenie
        if (acceptThread != null) {
            //Zakoncz je
            acceptThread!!.cancel()
            acceptThread = null
        }

        //Jesli sa wysylane jakies dane
        if (connectedThread != null)
        {
            //Zakoncz przesyl
            connectedThread!!.cancel()
            connectedThread = null
        }

        //Ustaw stan na "brak"
        setState(STATE_NONE)
    }

    //Funkcja odpowiedzialna za rozpoczęcie tworzenia nowego połąćzenia
    //Wywoływana jako pierwsza, po przejsciu z fragmentu
    //1
    fun connect(device: BluetoothDevice?){
        //Jesli istnieje jakies połączenie
        if (state == STATE_CONNECTED)
        {
            //Zakoncze je
            connectThread?.cancel()
            connectThread = null
        }

        //Rozpocznij nowe połączenie
        connectThread = ConnectThread(device)
        connectThread?.start()

        //Ustaw stan na łączenie...
        setState(STATE_CONNECTING)
    }

    //Funkcja do wywołania przesyłu danych przez BT
    fun write(buffer: ByteArray?) {
        var connThread: ConnectedThread
        synchronized(this) {
            if (state != STATE_CONNECTED) {
                return
            }
            connThread = connectedThread!!
        }
        //Przeslij dane
        connThread.write(buffer!!)
    }

    //Klasa odpowiedzialna za zaakceptowania połączenia wychodzącego z innego urządzenia
    private inner class AcceptThread : Thread(){
        //Stworz socket i rozpocznij nasłuchiwanie
        private var serverSocket: BluetoothServerSocket? = try{
            bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID)
        } catch (e: IOException){
            Log.e(TAG,"Accept->Constructor", e)
            null
        }

        //Funkcja odpowiadająca za akceptację połączenia
        override fun run(){
            var socket: BluetoothSocket? = null
            //Oczekiwanie na połączenia
            while (true) {
                try {
                    //Próba akceptacji połączenia
                    socket = serverSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Accept->Run", e)
                    try {
                        serverSocket?.close()
                    } catch (e1: IOException) {
                        Log.e(TAG, "Accept->Close", e1)
                    }
                }

                //Jeśli stworzono jakieś połączenie
                if (socket != null) {
                    //Wykonaj funkcję w zależności od stanu
                    when (gState()) {
                        //Jeśli nasłuchuje lub się łączy - wywołaj fukncję
                        STATE_LISTEN, STATE_CONNECTING -> connected(socket, socket.remoteDevice)
                        //Jeśli brak połączenia lub jest połączony
                        STATE_NONE, STATE_CONNECTED -> {
                            try {
                                //Zamknij socket
                                socket.close()
                            } catch (e: IOException) {
                                Log.e(TAG, "Accept->CloseSocket", e)
                            }
                        }
                    }
                }
            }
        }

        //Anulowanie połączenia
        fun cancel() {
            try {
                //Zamknij socket
                serverSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG,"Accept->CloseServer", e)
            }
        }
    }

    //Klasa odpowiedzialna za wywołanie połączenia BT - wysłanie zapytania do innego urządzenia
    private inner class ConnectThread(d: BluetoothDevice?) : Thread(){
        private var device: BluetoothDevice? = d

        //Stworz socket dla danego UUID
        private var socket: BluetoothSocket? = try {
            device?.createRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: IOException) {
            Log.e(TAG,"Connect->Constructor", e)
            null
        }

        //Funkcja odpowiedzialna za stworzenie połączenia
        override fun run(){
            try {
                //Wywołaj połączenie
                socket?.connect()
            } catch (e: IOException){
                try {
                    //Zamknij socket, jeśli się nie powiedzie
                    socket?.close()
                } catch (e1: IOException){
                    Log.e(TAG,"Connect->CloseSocket")
                }
                Log.e(TAG,"Connect->CloseSocket")
                //Wywołaj funkcje
                connectionFailed()
                return
            }

        @Synchronized
        //Po stworzeniu połączenia, wyzeruj zmienną odpowiedzialną za tę czynoność
        connectThread = null

        connected(socket, device)
        }

        //Anuluj połączenie
        fun cancel(){
            try {
                //Zamknij socket
                socket?.close()
            } catch (e: IOException){
                Log.e(TAG,"Connect->Cancel", e)
            }
        }

    }


    //Klasa odpowiedzialna za przesyłanie danych pomiędzy urządzeniami BT
    private inner class ConnectedThread(soc: BluetoothSocket?) : Thread(){
        private var socket: BluetoothSocket? = soc
        //Wejściowe dane
        private val inputStream: InputStream? = try {
            socket!!.inputStream
        } catch (e: IOException){
            null
        }
        //Wyjściowe dane
        private val outputStream: OutputStream? = try {
            socket!!.outputStream
        } catch (e: IOException){
            null
        }

        //Funkcja odpowiedzialna za odbieranie danych przesyłanych przez BT
        override fun run(){
            //Bufor typu ByteArray do odbioru danych
            val buffer = ByteArray(1024)
            val bytes: Int

            try {
                //Spróbuj odczytać dane z BT
                bytes = inputStream!!.read(buffer)
                //Prześlij odczytane dane do fragmentu
                handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_READ, bytes, -1, buffer).sendToTarget()
            } catch (e: IOException) {
                //Jesli sie nie udalo odczytac, wywołaj funkcje
                connectionLost()
            }
        }


        //Funkcja po stracie polaczenia
        private fun connectionLost() {
            //Wyslij informacje o stracie polaczenia do toasta w fragmencie
            val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_TOAST)
            val bundle = Bundle()
            bundle.putString(OnlineFragmentMenu.TOAST, "Connection lost")
            message.data = bundle
            handler?.sendMessage(message)

            //Anuluj połączenie
            this@GameUtils.start()
        }

        //Funkcja do wysłania danych przez BT
        fun write(buffer: ByteArray){
            try {
                outputStream?.write(buffer)
                handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_WRITE, -1, -1, buffer).sendToTarget()
            } catch (e: IOException) {
            }
        }

        //Anulowanie polaczenia
        fun cancel() {
            try {
                //Zamknij socket
                socket!!.close()
            } catch (e: IOException) {
            }
        }

    }


    //Funkcja wywołana po nieudanym polaczeniu
    @Synchronized
    private fun connectionFailed() {
        //Prześlij do fragmentu informacje o nieudanym polaczeniu
        val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(OnlineFragmentMenu.TOAST, "Cannot connect to the device. ")
        message.data = bundle
        handler!!.sendMessage(message)

        //Anuluj połączenie
        this.start()
    }

    //Funkcja po uzyskaniu połączenia BT
    @Synchronized
    private fun connected(socket: BluetoothSocket?, device: BluetoothDevice?) {
        //Jesli jest jakieś rozpoczęte połączenie
        if (connectThread != null)
        {
            //Zakończ je
            connectThread?.cancel()
            connectThread = null
        }

        //Jeśli jest przesył danych
        if (connectedThread != null)
        {
            //Zakoncz
            connectedThread!!.cancel()
            connectedThread = null
        }

        //Rozpocznij nowy przesył danych
        connectedThread = ConnectedThread(socket)
        connectedThread!!.start()

        //Prześlij informacje do fragmentu o uzyskaniu połączenia z urządzeniem o nazwie...
        val message: Message = handler!!.obtainMessage(OnlineFragmentMenu.MESSAGE_DEVICE_NAME)
        val bundle = Bundle()
        bundle.putString(OnlineFragmentMenu.DEVICE_NAME, device?.name)
        message.data = bundle
        handler!!.sendMessage(message)

        //Ustaw stan jako połączony
        setState(STATE_CONNECTED)
    }
}
