package com.example.dobble_projekt

import android.content.Context
import android.os.Handler


class GameUtils(c: Context?, h: Handler) {
    private var context: Context? = c
    private var handler: Handler? = h

    val STATE_NONE = 0
    val STATE_LISTEN = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3

    private var state = STATE_NONE
    //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    fun getState(): Int {
        return state
    }

    @Synchronized fun setState(s: Int){
        this.state = s
        handler?.obtainMessage(OnlineFragmentMenu.MESSAGE_STATE_CHANGED, state, -1)?.sendToTarget()
    }

    @Synchronized fun start(){

    }

    @Synchronized fun stop(){

    }

}