package com.example.dobble_projekt

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.dobble_projekt.databinding.FragmentOfflineGameBinding
import java.lang.Math.*
import java.util.*
import kotlin.collections.ArrayList

class OfflineGameFragment : Fragment() {

    private var layoutReady: Boolean = false
    private lateinit var binding: FragmentOfflineGameBinding
    var numOfSymbols: Int = 8
    var n = numOfSymbols - 1
    var cards: ArrayList<ArrayList<Int>> = ArrayList()
    var card: ArrayList<Int> = ArrayList()
    var top_card: Int = 0
    var bottom_card: Int = 0
    var scale: ArrayList<Float> = ArrayList()
    var topImages: MutableList<ImageView> = ArrayList()
    var bottomImages: MutableList<ImageView> = ArrayList()
    var screenHeight: Int = -1
    var screenWidth: Int = -1
    var correctIndex: Int = 0
    var cardT: ArrayList<Int> = ArrayList()
    var cardB: ArrayList<Int> = ArrayList()
    var resultTop: Int = 0
    var resultBottom: Int = 0
    var flag: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfflineGameBinding.inflate(inflater, container, false)

        //Rozpoczęcie tworzenia tablicy o rozmiarze 57x8, w której każdy wiersz ma tylko jedną wspólną wartość z każdym pozostałym
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
        //Koniec

        //Tworzenie tablicy z różnymi wartościami skali obrazków
        for (i in 1..numOfSymbols) {
            scale.add(0.65F + i / 30F)
        }

        //Przypisywanie ośmiu obiektów ImageView do listy
        topImages.add(binding.image1)
        topImages.add(binding.image2)
        topImages.add(binding.image3)
        topImages.add(binding.image4)
        topImages.add(binding.image5)
        topImages.add(binding.image6)
        topImages.add(binding.image7)
        topImages.add(binding.image8)
        //Przypisywanie kolejnych ośmiu obiektów ImageView do listy
        bottomImages.add(binding.image9)
        bottomImages.add(binding.image10)
        bottomImages.add(binding.image11)
        bottomImages.add(binding.image12)
        bottomImages.add(binding.image13)
        bottomImages.add(binding.image14)
        bottomImages.add(binding.image15)
        bottomImages.add(binding.image16)

        //Losowanie indeksu wiersza z tablicy 57x8 - losowanie karty
        top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1)) //index duzej tablicy
        bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))
        //Sprawdzenie czy nie wylosowano dwóch takich samych kart
        if (bottom_card == top_card)
        {
            while (bottom_card == top_card)
            {
                bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))
            }
        }

        //Sprawdzenie, jaki obrazek jest taki sam na obu kartach
        correctIndex = getIndex(top_card, bottom_card)

        //Przypisanie 0 do wyniku każdego z graczy
        binding.resultBottom.text = resultBottom.toString()
        binding.resultTop.text = resultTop.toString()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Przypisanie wartości wysokości i szerokości ekranu do zmiennych
        binding.constraintLayout.viewTreeObserver.addOnGlobalLayoutListener {
            screenHeight = binding.constraintLayout.height
            screenWidth = binding.constraintLayout.width
            layoutReady = true
            //Wyświetlenie obrazków na ekranie przy pierwszy uruchomieniu
            if (flag) {
                showImages(top_card, true, false)
                showImages(bottom_card, false, true)
                flag = false
            }
            binding.constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener { this }
        }

        //Nasłuchiwacze przypisane do każdego obrazka na ekranie
        binding.image1.setOnClickListener {
            correctChoiceTop(0)
        }
        binding.image2.setOnClickListener {
            correctChoiceTop(1)
        }
        binding.image3.setOnClickListener {
            correctChoiceTop(2)
        }
        binding.image4.setOnClickListener {
            correctChoiceTop(3)
        }
        binding.image5.setOnClickListener {
            correctChoiceTop(4)
        }
        binding.image6.setOnClickListener {
            correctChoiceTop(5)
        }
        binding.image7.setOnClickListener {
            correctChoiceTop(6)
        }
        binding.image8.setOnClickListener {
            correctChoiceTop(7)
        }


        binding.image9.setOnClickListener {
            correctChoiceBottom(0)
        }
        binding.image10.setOnClickListener {
            correctChoiceBottom(1)
        }
        binding.image11.setOnClickListener {
            correctChoiceBottom(2)
        }
        binding.image12.setOnClickListener {
            correctChoiceBottom(3)
        }
        binding.image13.setOnClickListener {
            correctChoiceBottom(4)
        }
        binding.image14.setOnClickListener {
            correctChoiceBottom(5)
        }
        binding.image15.setOnClickListener {
            correctChoiceBottom(6)
        }
        binding.image16.setOnClickListener {
            correctChoiceBottom(7)
        }


    }

    private fun correctChoiceBottom(i: Int)
    {
        //SPrawdzenie czy wybrano poprawny obrazek
        if (correctIndex == cards[bottom_card][i])
        {
            //Zwiekszenie wyniku o 1
            resultBottom++
            binding.resultBottom.text = resultBottom.toString()
            //Czy osiągnieto wynik 29
            if (resultBottom % 29 == 0) {
                //Wywołanie funkcji odpowiedzialnej za wyświetlenie Alert Dialog
                wonFun(true)
                //Losowanie nowej karty
                bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                if (bottom_card == top_card) {
                    while (bottom_card == top_card) {
                        bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                    }
                }
                //Sprawdzanie poprawnego indeksu
                correctIndex = getIndex(top_card, bottom_card)
                //Wyswietlenie obrazków
                showImages(bottom_card, false, true)
            }
            //Nie osiągnięto wyniku 29
            else {
                //Losowanie nowej karty
                bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                if (bottom_card == top_card)
                {
                    while (bottom_card == top_card)
                    {
                        bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))
                    }
                }
                //Sprawdzanie poprawnego indeksu
                correctIndex = getIndex(top_card, bottom_card)
                //Wyswietlenie obrazków
                showImages(bottom_card, false, true)
            }
        }
    }

    private fun wonFun(flag: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        if (flag){
            builder.setMessage("You have won the game! :) ")
        }
        else {
            builder.setMessage("You have not won the game! :( ")
        }
        //Jesli wybrano ten przycisk, gra jest kontynuowana
        builder.setPositiveButton("Continue playing", DialogInterface.OnClickListener{
            dialog: DialogInterface?, which: Int ->  
        })
        //Jesli ten, gra jest przerywana i gracz wraca do menu
        builder.setNegativeButton("Menu", DialogInterface.OnClickListener {
                dialog: DialogInterface?, which: Int -> goToMenu()  })
        builder.show()
    }

    private fun goToMenu() {
        findNavController().navigate(R.id.action_offlineGameFragment_to_chooseGameFragment)
    }

    private fun correctChoiceTop(i: Int)
    {
        if (correctIndex == cards[top_card][i])
        {
            resultTop++
            binding.resultTop.text = resultTop.toString()
            if (resultTop % 29 == 0) {
                wonFun(false)
                top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                if (bottom_card == top_card)
                {
                    while (bottom_card == top_card)
                    {
                        top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))
                    }
                }
                correctIndex = getIndex(top_card, bottom_card)
                showImages(top_card, true, false)
            }
            else {
                top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                if (bottom_card == top_card)
                {
                    while (bottom_card == top_card)
                    {
                        top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))
                    }
                }
                correctIndex = getIndex(top_card, bottom_card)
                showImages(top_card, true, false)
            }
        }
    }

    private fun getIndex(top: Int, bottom: Int): Int {
        cardT = cards[top]
        cardB = cards[bottom]
        //Sprawdzanie, które wartości z dwóch tablic się pokrywają
        val help = cardT.filter(cardB::contains).toList()
        //Zwracanie pierwszej z tych wartości (zawsze jest tylko jedna)
        return help[0]
    }

    private fun rotateImg(): Float {
        //Zwraca losową wartość kątu, o jaki ma być obrócony obrazek
        return Random().nextFloat() * Int.MAX_VALUE % 360
    }

    private fun showImages(ind: Int, top: Boolean, bottom: Boolean){
        //Przypisz do zmiennej jeden z wierszy dużej tablicy 57x8
        card = cards[ind]
        //Pomieszaj wartości w wierszu
        card.shuffle()
        //Pętla po wszystkich obrazkach na karcie
        for (i in 0 until 8){
            if (top){
                //Przypisanie obrazka do ImageView
                topImages[i].setImageResource(resolveDrawable(card[i]))
                //Ustawienie widoczności
                topImages[i].visibility = VISIBLE
                //Obrot obrazka
                topImages[i].rotation = rotateImg()
                //Skala obrazka
                topImages[i].scaleX = scale[(1 + rotateImg()%8).toInt()]
                topImages[i].scaleY = topImages[i].scaleX

                //Umiejscowienie na karcie
                when (i)
                {
                    0 -> {  //środek prawo
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat()
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) + 0.1F * screenWidth
                    }
                    1 -> { //gora lewo
                        topImages[i].y =(screenHeight / 4 - topImages[i].height/2).toFloat() - 0.15F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) - 0.05F * screenWidth
                    }
                    2 -> { //dol prawo
                        topImages[i].y =(screenHeight / 4 - topImages[i].height/2).toFloat() + 0.15F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) + 0.05F * screenWidth
                    }
                    3 -> {//? lewo gora
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat() - 0.09F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) - 0.26F * screenWidth
                    }
                    4 -> {//? prawo gora
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat() - 0.09F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) + 0.26F * screenWidth
                    }
                    5 -> {
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat() + 0.1F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) + 0.25F * screenWidth
                    }
                    6 -> {
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat() + 0.1F * screenHeight
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) - 0.25F * screenWidth
                    }
                    7 -> {
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat()
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat()) - 0.13F * screenWidth
                    }
                    else -> {
                        topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat()
                        topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat())
                    }
                }

                /*if (i == 0){
                    topImages[i].y = (screenHeight / 4 - topImages[i].height/2).toFloat()
                    topImages[i].x = ((screenWidth / 2 - topImages[i].width/2).toFloat())
                }
                else
                {
                    topImages[i].y = setPosition(i, true).toFloat() - topImages[i].height/2
                    topImages[i].x = setPosition(i, false).toFloat() - topImages[i].width/2
                    val rc1: Rect = Rect()
                    val rc2: Rect = Rect()
                    var j: Int = 0
                    while (j < i) {
                        rc1.bottom = (topImages[i].y + topImages[i].height).toInt()
                        rc1.top = topImages[i].y.toInt()
                        rc1.left = topImages[i].x.toInt()
                        rc1.right = (topImages[i].x + topImages[i].width).toInt()
                        rc2.bottom = (topImages[j].y + topImages[j].height).toInt()
                        rc2.top = topImages[j].y.toInt()
                        rc2.left = topImages[j].x.toInt()
                        rc2.right = (topImages[j].x + topImages[j].width).toInt()
                        if (Rect.intersects(rc1, rc2)) {
                            j = 0
                            topImages[i].y = setPosition(i, true).toFloat() - topImages[i].height/2
                            topImages[i].x = setPosition(i, false).toFloat() - topImages[i].width/2
                        }
                        else
                            j++
                    }
                }*/
            }
            if (bottom){
                bottomImages[i].setImageResource(resolveDrawable(card[i]))
                bottomImages[i].visibility = VISIBLE
                bottomImages[i].rotation = rotateImg()
                bottomImages[i].scaleX = scale[(1 + rotateImg()%8).toInt()]
                bottomImages[i].scaleY = bottomImages[i].scaleX

                when (i)
                {
                    0 -> {  //środek
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat()) + 0.1F * screenWidth
                    }
                    1 -> { //gora lewo
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()- 0.15F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat())  - 0.05F * screenWidth
                    }
                    2 -> { //dol prawo
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()+ 0.15F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat())  + 0.05F * screenWidth
                    }
                    3 -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat() - 0.09F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat())  - 0.26F * screenWidth
                    }
                    4 -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat() - 0.09F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat()) + 0.26F * screenWidth
                    }
                    5 -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat() + 0.1F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat()) + 0.25F * screenWidth
                    }
                    6 -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat() + 0.1F * screenHeight
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat()) - 0.25F * screenWidth
                    }
                    7 -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat()) - 0.13F * screenWidth
                    }
                    else -> {
                        bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()
                        bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat())
                    }
                }
                /*if (i == 0){
                    bottomImages[i].y = ((screenHeight / 4 - bottomImages[i].height/2) + screenHeight/2).toFloat()
                    bottomImages[i].x = ((screenWidth / 2 - bottomImages[i].width/2).toFloat())
                }
                else {
                    bottomImages[i].y = setPosition(i, true).toFloat() - bottomImages[i].height/2 + screenHeight/2
                    bottomImages[i].x = setPosition(i, false).toFloat() - bottomImages[i].width/2
                    val rc1: Rect = Rect()
                    val rc2: Rect = Rect()
                    var j: Int = 0
                    while (j < i) {
                        rc1.bottom = (bottomImages[i].y + bottomImages[i].height).toInt()
                        rc1.top = bottomImages[i].y.toInt()
                        rc1.left = bottomImages[i].x.toInt()
                        rc1.right = (bottomImages[i].x + bottomImages[i].width).toInt()
                        rc2.bottom = (bottomImages[j].y + bottomImages[j].height).toInt()
                        rc2.top = bottomImages[j].y.toInt()
                        rc2.left = bottomImages[j].x.toInt()
                        rc2.right = (bottomImages[j].x + bottomImages[j].width).toInt()
                        if (Rect.intersects(rc1, rc2)) {
                            j = 0
                            bottomImages[i].y = setPosition(i, true).toFloat() - bottomImages[i].height/2 + screenHeight/2
                            bottomImages[i].x = setPosition(i, false).toFloat() - bottomImages[i].width/2
                        }
                        else
                            j++
                    }
                }*/
            }
        }
    }

    private fun setPosition(i: Int, xy: Boolean): Double {
        var R: Double = 0.35 * screenWidth//abs(Random().nextFloat() * Float.MAX_VALUE % (0.25F * screenWidth)) + 0.1F * screenWidth
        var alfa: Double = i + abs(Random().nextFloat() * Double.MAX_VALUE % (PI * i * 0.75))
        if(xy) {
            var y: Double = screenHeight * 0.25 + 0.9 * R * sin(alfa)
            return y
        }
        else {
            var x: Double = screenWidth * 0.5 + 0.9 * R * kotlin.math.cos(alfa)
            return x
        }
    }

    //Funkcja przyjmująca obrazki i przypisująca im indeksy
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

}