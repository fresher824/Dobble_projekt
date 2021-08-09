package com.example.dobble_projekt.placeholder

class PairedDevice {
    var deviceName: String = ""
    var deviceAddress: String = ""
    fun init(name: String, address: String) {
        deviceName = name
        deviceAddress = address
    }
}