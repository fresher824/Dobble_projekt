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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Ustawienie filtrów i rejestracja nasłuchiwaczy/odbiornikow
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, filter1)

        //Ustawienie nasluchiwacza na przycisk
        binding.button3.setOnClickListener { scanDevices() }

        //Wywołanie funkcji wyswietlającej
        init()
    }

    private fun init()
    {
        //Przypisanie obiektów do zmiennych
        listPairedDevices = binding.listPairedDevices
        listAvailableDevices = binding.listAvailableDevices
        progressScanDevices = binding.progressScanDevices

        adapterPairedDevices = ArrayAdapter<String>(this, R.layout.device_list_item)
        adapterAvailableDevices = ArrayAdapter<String>(this, R.layout.device_list_item)

        //Przypisanie adapterów do zmiennych
        listPairedDevices.adapter = adapterPairedDevices
        listAvailableDevices.adapter = adapterAvailableDevices

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //Wyszukiwanie sparowanych urządzeń
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        //Sprawdzanie czy występują takie urządzenia
        if (pairedDevices != null && pairedDevices.size > 0)
        {
            //Jesli tak, dodajemy do listy urządzen sparowanych
            for (device in pairedDevices)
            {
                adapterPairedDevices.add(device.name + "\n" + device.address)
            }
        }

        //Ustawienie nasluchiwacza na dostępnych urządzeniach
        listAvailableDevices.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                //Pobranie adresu urządzenia i przeslanie adresu do fragmentu, z którego wywołana jest aktywnosc
                val info = (view as TextView).text.toString()
                val address = info.substring(info.length - 17)
                val intent = Intent()
                intent.putExtra("deviceAddress", address)
                setResult(RESULT_OK, intent)
                finish()
            }
        listPairedDevices.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                //Pobranie adresu urządzenia i przeslanie adresu do fragmentu, z którego wywołana jest aktywnosc
                val info = (view as TextView).text.toString()
                val address = info.substring(info.length - 17)
                val intent = Intent()
                intent.putExtra("deviceAddress", address)
                setResult(RESULT_OK, intent)
                finish()
            }
    }

    //Odbiornik/nasluchiwacz dostepnych urzadzen
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action)
            {
                //Jesli znaleziono urządzenie w poblizu
                BluetoothDevice.ACTION_FOUND ->
                {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    //Przypisz urzadzenie do zmiennej
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    //Jesli urzadzenie nie jest sparowane, dodaj je do listy urzadzen dostepnych
                    if (device!!.bondState != BluetoothDevice.BOND_BONDED) {
                        adapterAvailableDevices.add(device.name + "\n" + device.address)
                    }
                }
                //Jesli zakonczono wyszukiwanie
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->
                {
                    //Ukryj progressBar
                    progressScanDevices.visibility = GONE
                    //Wyswietl informację czy znaleziono urządzenia czy nie
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
        //Wyswietl progressBar
        progressScanDevices.visibility = VISIBLE
        //Wyczysc liste dostepnych urzadzen
        adapterAvailableDevices.clear()
        Toast.makeText(this, "Scan started", Toast.LENGTH_SHORT).show()

        //Jesli nie zakonczono poprzedniego wyszukiwania
        if (bluetoothAdapter.isDiscovering)
        {
            //Zakoncz je
            bluetoothAdapter.cancelDiscovery()
        }
        //Rozpocznij wyszukiwanie
        bluetoothAdapter.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Wyrejestruj nasluchiwacz/odbiornik
        this.unregisterReceiver(receiver)
    }

}