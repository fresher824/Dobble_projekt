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

        topImages.add(binding.image1)
        topImages.add(binding.image2)
        topImages.add(binding.image3)
        topImages.add(binding.image4)
        topImages.add(binding.image5)
        topImages.add(binding.image6)
        topImages.add(binding.image7)
        topImages.add(binding.image8)

        bottomImages.add(binding.image9)
        bottomImages.add(binding.image10)
        bottomImages.add(binding.image11)
        bottomImages.add(binding.image12)
        bottomImages.add(binding.image13)
        bottomImages.add(binding.image14)
        bottomImages.add(binding.image15)
        bottomImages.add(binding.image16)


        top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1)) //index duzej tablicy
        bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size-1))

        correctIndex = getIndex(top_card, bottom_card)

        binding.resultBottom.text = resultBottom.toString()
        binding.resultTop.text = resultTop.toString()

        //cardT.retainAll(cardB)
        //correctIndex = cardT[0]

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.constraintLayout.viewTreeObserver.addOnGlobalLayoutListener {
            screenHeight = binding.constraintLayout.height
            screenWidth = binding.constraintLayout.width
            layoutReady = true
            if (flag) {
                showImages(top_card, true, false)
                showImages(bottom_card, false, true)
                flag = false
            }
            binding.constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener { this }
        }

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
        if (correctIndex == cards[bottom_card][i])
        {
            resultBottom++
            binding.resultBottom.text = resultBottom.toString()
            if (resultBottom % 5 == 0)
                wonFun(true)
            else {
                bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                correctIndex = getIndex(top_card, bottom_card)
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
        builder.setPositiveButton("Continue playing", DialogInterface.OnClickListener{
            dialog: DialogInterface?, which: Int ->  
        })
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
            if (resultTop % 5 == 0)
                wonFun(false)
            else {
                top_card = abs(Random().nextInt() * Int.MAX_VALUE % (cards.size - 1))
                correctIndex = getIndex(top_card, bottom_card)
                showImages(top_card, true, false)
            }
        }
    }

    private fun getIndex(top: Int, bottom: Int): Int {
        cardT = cards[top]
        cardB = cards[bottom]

        val help = cardT.filter(cardB::contains).toList()
        return help[0]
    }

    private fun rotateImg(): Float {
        return Random().nextFloat() * Int.MAX_VALUE % 360
    }

    private fun showImages(ind: Int, top: Boolean, bottom: Boolean){
        card = cards[ind]
        for (i in 0 until 8){
            if (top){
                topImages[i].setImageResource(resolveDrawable(card[i]))
                topImages[i].visibility = VISIBLE
                topImages[i].rotation = rotateImg()
                topImages[i].scaleX = scale[(1 + rotateImg()%8).toInt()]
                topImages[i].scaleY = topImages[i].scaleX

                when (i)
                {
                    0 -> {  //środek
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
            9 -> R.drawable.byk
            10 -> R.drawable.cebula
            11 -> R.drawable.cytryna
            12 -> R.drawable.czosnek
            13 -> R.drawable.golab
            14 -> R.drawable.groszek
            15 -> R.drawable.jablkoczerwone
            16 -> R.drawable.jablkolisc
            17 -> R.drawable.jablkomalinowka
            18 -> R.drawable.jablkopapierowka
            19 -> R.drawable.jezyny
            20 -> R.drawable.kaczka
            21 -> R.drawable.kapusta
            22 -> R.drawable.kiwi
            23 -> R.drawable.kogut
            24 -> R.drawable.kokos
            25 -> R.drawable.kokoszamkniety
            26 -> R.drawable.kon
            27 -> R.drawable.kot
            28 -> R.drawable.krolik
            29 -> R.drawable.krowa
            30 -> R.drawable.kurka
            31 -> R.drawable.limonka
            32 -> R.drawable.limonkazamknieta
            33 -> R.drawable.maliny
            34 -> R.drawable.motyl
            35 -> R.drawable.ogorek
            36 -> R.drawable.ogorekrzeczywisty
            37 -> R.drawable.osa
            38 -> R.drawable.zielonecos
            39 -> R.drawable.paprykachili
            40 -> R.drawable.paprykaczerwona
            41 -> R.drawable.paprykapomaranczowa
            42 -> R.drawable.paprykazolta
            43 -> R.drawable.paw
            44 -> R.drawable.pies
            45 -> R.drawable.pomarancza
            46 -> R.drawable.pomidor
            47 -> R.drawable.pomidorrzeczywisty
            48 -> R.drawable.por
            49 -> R.drawable.prosiak
            50 -> R.drawable.przekrojonejablko
            51 -> R.drawable.salata
            52 -> R.drawable.sliwka
            53 -> R.drawable.szparagi
            54 -> R.drawable.truskawki
            55 -> R.drawable.winogoronoczerwone
            56 -> R.drawable.winogoronozielone
            else -> R.drawable.ziemniaki
        }
    }

}