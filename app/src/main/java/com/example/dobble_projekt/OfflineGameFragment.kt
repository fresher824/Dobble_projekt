package com.example.dobble_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import com.example.dobble_projekt.databinding.FragmentOfflineGameBinding
import java.lang.Math.abs
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList

class OfflineGameFragment : Fragment() {

    private var layoutReady: Boolean = false
    private lateinit var binding: FragmentOfflineGameBinding
    var numOfSymbols: Int = 8
    var n = numOfSymbols - 1
    var cards: ArrayList<ArrayList<Int>> = ArrayList()
    var card: ArrayList<Int> = ArrayList()
    var flags: ArrayList<Boolean> = ArrayList()
    var top_card: Int = 0
    var bottom_card: Int = 0
    var scale: ArrayList<Float> = ArrayList()
    var topImages: MutableList<ImageView> = ArrayList()
    var bottomImages: MutableList<ImageView> = ArrayList()
    var screenHeight: Int = -1
    var screenWidth: Int = -1

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

        for (i in 1..cards.size){
            flags.add(false)
        }

        for (i in 1..numOfSymbols) {
            scale.add(0.4F + i / 40F)
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


        top_card = abs(Random().nextInt() * Int.MAX_VALUE % cards.size)
        bottom_card = abs(Random().nextInt() * Int.MAX_VALUE % cards.size)


        //showImages(bottom_card, false, true)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.constraintLayout.viewTreeObserver.addOnGlobalLayoutListener {
            screenHeight = binding.constraintLayout.height
            screenWidth = binding.constraintLayout.width
            layoutReady = true
            showImages(top_card, true, false)
            binding.constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener { this }
        }
    }

    private fun rotateImg(): Float {
        return Random().nextFloat() * Int.MAX_VALUE % 360
        //binding.imageView3.rotation = toDegrees
    }

    private fun showImages(ind: Int, top: Boolean, bottom: Boolean){
        card = cards[ind]
        for (i in 1 until 8){
            if (top){
                val img:ImageView = topImages[i]
                img.setImageResource(resolveDrawable(card[i]))
                img.visibility = VISIBLE
                img.rotation = rotateImg()
                img.scaleX = scale[(1 + rotateImg()%8).toInt()]
                img.scaleY = topImages[i].scaleX
                topImages[i].x = setPosition(i, true)
                topImages[i].y = setPosition(i, false)
            }
            if (bottom){
                val img: ImageView = bottomImages[i]
                img.setImageResource(resolveDrawable(card[i]))
                bottomImages[i].visibility = VISIBLE
                bottomImages[i].rotation = rotateImg()
                bottomImages[i].scaleX = scale[(1 + rotateImg()%8).toInt()]
                bottomImages[i].scaleY = bottomImages[i].scaleX
                topImages[i].x = setPosition(i, true) * 3
                topImages[i].y = setPosition(i, false)
            }
        }
        flags[top_card] = true
        flags[bottom_card] = true
    }

    private fun setPosition(i: Int, xy: Boolean): Float {
        if(xy)
            return (screenHeight/4).toFloat()
        else
            return (screenWidth/2).toFloat()
    }

    private fun imgPositionTop(){

    }

    private fun imgPositionBottom(){

    }

    fun resolveDrawable(value: Int): Int {
        return when(value) {
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
            57 -> R.drawable.ziemniaki
            else -> R.drawable.ziemniaki
        }
    }

}