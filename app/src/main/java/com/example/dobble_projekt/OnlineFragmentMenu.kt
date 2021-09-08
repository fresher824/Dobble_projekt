package com.example.dobble_projekt

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.dobble_projekt.databinding.FragmentOnlineMenuBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class OnlineFragmentMenu : Fragment() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: FragmentOnlineMenuBinding
    private val SELECT_DEVICE = 102
    private var gameUtils: GameUtils? = null

    //Game
    private var layoutReady: Boolean = false
    var numOfSymbols: Int = 8
    var n = numOfSymbols - 1
    var cards: ArrayList<ArrayList<Int>> = ArrayList()
    var card: ArrayList<Int> = ArrayList()
    var your_card: Int = 0
    var my_card: Int = 0
    var scale: ArrayList<Float> = ArrayList()
    var yourImages: MutableList<ImageView> = ArrayList()
    var myImages: MutableList<ImageView> = ArrayList()
    var screenHeight: Int = -1
    var screenWidth: Int = -1
    var correctIndex: Int = 0
    var cardY: ArrayList<Int> = ArrayList()
    var cardM: ArrayList<Int> = ArrayList()
    var resultYour: Int = 0
    var resultMy: Int = 0
    var flag: Boolean = true
    var createrFlag: Boolean = false

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
        //Sprawdzenie czy bluetooth jest włączony
        if (bluetoothAdapter.isEnabled)
        {
            val img: ImageView = binding.bluetoothView
            img.setImageResource(R.drawable.bluetooth_on)
        }

        //Game
        for (i in 1..(n+1)){
            card.add(i)
        }
        cards.add(card)

        for (j in 1..n){
            card = ArrayList()
            card.add(1)
            for (k in 1..n){
                card.add((n + n * (j-1) + k+1))
            }
            cards.add(card)
        }

        for (i in 1..n){
            for (j in 1..n){
                card = ArrayList()
                card.add(i+1)
                for (k in 1..n){
                    card.add(n + 2 + n * (k-1) + (((i-1) * (k-1) +j-1) % n))
                }
                cards.add(card)
            }
        }

        for (i in 1..numOfSymbols) {
            scale.add(0.65F + i / 30F)
        }

        yourImages.add(binding.imageOnline1)
        yourImages.add(binding.imageOnline2)
        yourImages.add(binding.imageOnline3)
        yourImages.add(binding.imageOnline4)
        yourImages.add(binding.imageOnline5)
        yourImages.add(binding.imageOnline6)
        yourImages.add(binding.imageOnline7)
        yourImages.add(binding.imageOnline8)

        myImages.add(binding.imageOnline9)
        myImages.add(binding.imageOnline10)
        myImages.add(binding.imageOnline11)
        myImages.add(binding.imageOnline12)
        myImages.add(binding.imageOnline13)
        myImages.add(binding.imageOnline14)
        myImages.add(binding.imageOnline15)
        myImages.add(binding.imageOnline16)


        your_card =
            Math.abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1)) //index duzej tablicy
        my_card = Math.abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
        if (my_card == your_card)
        {
            while (my_card == your_card)
            {
                my_card = Math.abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
            }
        }

        correctIndex = getIndex(your_card, my_card)

        binding.resultOnlineBottom.text = resultMy.toString()
        binding.resultOnlineTop.text = resultYour.toString()
        //EndGame

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Po otrzymaniu adresu z aktywności, wywołaj funkcję mającą połączyć oba urządzenia
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            val address = data?.getStringExtra("deviceAddress")
            gameUtils?.connect(bluetoothAdapter.getRemoteDevice(address))
            createrFlag = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Nasluchiwacz obrazka z logo BT
        binding.bluetoothView.setOnClickListener {
            updateBTImage()
        }
        //Nasluchiwacz przycisku "Create Game"
        binding.createButton.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                //Przekierowanie do aktywności wyboru urządzenia
                val intent: Intent = Intent(activity, chooseDevice::class.java)
                startActivityForResult(intent, SELECT_DEVICE)
                //findNavController().navigate(R.id.action_onlineFragmentMenu_to_deviceListFragment)
            }
            //Przypomnienie o wlaczeniu BT
            else
                Snackbar.make(binding.root, "Turn on bluetooth!", Snackbar.LENGTH_LONG).show()
        }
        //Nasluchiwacz przycisku "Join Game" - ustawienie wykrywalnosci na 5 minut
        binding.joinButton.setOnClickListener {
            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
            startActivity(discoverableIntent)
            Toast.makeText(context, "Your device is now discoverable for 300 seconds", Toast.LENGTH_SHORT).show()
            gameUtils?.start()  //Rozpocznij nasluchiwanie na proby polaczenia
        }


        //Game

        binding.imageOnline1.setOnClickListener {
            if (createrFlag)
            {
                if (correctIndex == cards[my_card][1])
                {
                    resultMy++
                    binding.resultOnlineBottom.text = resultMy.toString()
                    your_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                    if (my_card == your_card)
                    {
                        while (my_card == your_card)
                        {
                            my_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                        }
                    }
                    correctIndex = getIndex(your_card, my_card)
                    val array: ArrayList<Byte> = ArrayList()
                    for (i in 0..1) {
                        if (i == 0)
                        {
                            for (j in 0..7) {
                                array.add(cards[my_card][j].toByte())
                            }
                        }
                        else {
                            for (j in 0..7) {
                                array.add(cards[your_card][j].toByte())
                            }
                        }
                    }
                    if (!array.isEmpty())
                    {
                        gameUtils?.write(array.toByteArray())
                    }
                    //Pokaż obrazki
                }
            }
            else
            {
                val id: ArrayList<Byte> = ArrayList()
                id.add(cards[my_card][1].toByte())
                if (!id.isEmpty())
                {
                    gameUtils?.write(id.toByteArray())
                }
            }
        }
        binding.imageOnline2.setOnClickListener {

        }
        binding.imageOnline3.setOnClickListener {

        }
        binding.imageOnline4.setOnClickListener {

        }
        binding.imageOnline5.setOnClickListener {

        }
        binding.imageOnline6.setOnClickListener {

        }
        binding.imageOnline7.setOnClickListener {

        }
        binding.imageOnline8.setOnClickListener {

        }

        //EndGame


    }



    private fun updateBTImage(){
        val img: ImageView = binding.bluetoothView
        //Przypisz nowy obrazek do ImageView z logo BT
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

    //Wykorzystywany do komunikacji z funkcjami odpowiedzialnymi za przesyłanie danych i łączenie się z innym urządzeniem
    private val handler = Handler { message ->
        when (message.what) {
            MESSAGE_STATE_CHANGED -> when (message.arg1) {
                /*ChatUtils.STATE_NONE -> setState("Not Connected")
                ChatUtils.STATE_LISTEN -> setState("Not Connected")
                ChatUtils.STATE_CONNECTING -> setState("Connecting...")
                ChatUtils.STATE_CONNECTED -> setState("Connected: $connectedDevice")*/
            }
            /*MESSAGE_WRITE -> {
                val buffer1 = message.obj as ByteArray
            }*/
            //Co się dzieje po odczytaniu wiadomosci przeslanej przez BT
            MESSAGE_READ -> {
                val buffer = message.obj as ByteArray
                //val inputBuffer = String(buffer, 0, message.arg1)
            }
            //Wyswietla toast z nazwą urządzenia, z którym się połączono
            MESSAGE_DEVICE_NAME -> {
               connectedDevice =
                   message.data.getString(DEVICE_NAME)!!
                Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show()
            }
            //Wyswietla informację nadaną przez funkcję z gameUtils
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

    //Game
    private fun getIndex(top: Int, bottom: Int): Int {
        cardY = cards[top]
        cardM = cards[bottom]

        val help = cardY.filter(cardM::contains).toList()
        return help[0]
    }



    fun resolveDrawable(value: Int): Int {
        return when(value) {
            0 -> R.drawable.ziemniaki
            1 -> R.drawable.arbuz
            2 -> R.drawable.baklazan
            3 -> R.drawable.baklazanzeczywisty
            4 -> R.drawable.borowka
            5 -> R.drawable.brokul
            6 -> R.drawable.brzoskwinia
            7 -> R.drawable.burak
            8 -> R.drawable.byk
            9  -> R.drawable.cebula
            10 -> R.drawable.cytryna
            11 -> R.drawable.czosnek
            12 -> R.drawable.golab
            13 -> R.drawable.groszek
            14 -> R.drawable.jablkoczerwone
            15 -> R.drawable.jablkolisc
            16 -> R.drawable.jablkomalinowka
            17 -> R.drawable.jablkopapierowka
            18 -> R.drawable.jezyny
            19 -> R.drawable.kaczka
            20 -> R.drawable.kapusta
            21 -> R.drawable.kiwi
            22 -> R.drawable.kogut
            23 -> R.drawable.kokos
            24 -> R.drawable.kokoszamkniety
            25 -> R.drawable.kon
            26 -> R.drawable.kot
            27 -> R.drawable.krolik
            28 -> R.drawable.krowa
            29 -> R.drawable.kurka
            30 -> R.drawable.limonka
            31 -> R.drawable.limonkazamknieta
            32 -> R.drawable.maliny
            33 -> R.drawable.motyl
            34 -> R.drawable.ogorek
            35 -> R.drawable.ogorekrzeczywisty
            36 -> R.drawable.osa
            37 -> R.drawable.zielonecos
            38 -> R.drawable.paprykachili
            39 -> R.drawable.paprykaczerwona
            40 -> R.drawable.paprykapomaranczowa
            41 -> R.drawable.paprykazolta
            42 -> R.drawable.paw
            43 -> R.drawable.pies
            44 -> R.drawable.pomarancza
            45 -> R.drawable.pomidor
            46 -> R.drawable.pomidorrzeczywisty
            47 -> R.drawable.por
            48 -> R.drawable.prosiak
            49 -> R.drawable.przekrojonejablko
            50 -> R.drawable.salata
            51 -> R.drawable.sliwka
            52 -> R.drawable.szparagi
            53 -> R.drawable.truskawki
            54 -> R.drawable.winogoronoczerwone
            55 -> R.drawable.winogoronozielone
            56 -> R.drawable.owca
            else -> R.drawable.ziemniaki
        }
    }
    //EndGame

    /* Informacje do przesłania:
       a) Od kreującego grę do drugiego
        - indeksy obrazków obu graczy
        -
       b) Od drugiego do kreującego
        - indeks wybranego obrazka
        -
    */
}

