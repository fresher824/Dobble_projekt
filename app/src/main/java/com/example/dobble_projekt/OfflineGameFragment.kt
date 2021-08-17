package com.example.dobble_projekt

import android.graphics.ColorMatrix
import android.graphics.Matrix
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dobble_projekt.databinding.FragmentOfflineGameBinding
import com.example.dobble_projekt.databinding.FragmentOnlineMenuBinding
import java.util.*
import kotlin.collections.ArrayList

class OfflineGameFragment : Fragment() {

    private lateinit var binding: FragmentOfflineGameBinding
    var numOfSymbols: Int = 8
    var n = numOfSymbols - 1
    var cards: ArrayList<Any> = ArrayList()
    var card: ArrayList<Int> = ArrayList()

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

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        rotateImg()
        scaleImg()
    }

    private fun rotateImg(){
        val toDegrees: Float = Random().nextFloat() * Int.MAX_VALUE % 360
        binding.imageView3.rotation = toDegrees
    }

    private fun scaleImg(){
        val scale: Double = Random().nextFloat() * Int.MAX_VALUE % 0.8 + 0.4
        binding.imageView3.scaleX = scale.toFloat()
        binding.imageView3.scaleY = scale.toFloat()
    }

    private fun imgPositionTop(){

    }

    private fun imgPositionBottom(){

    }

    /*fun resolveDrawable(value: Int): Int {
        return when(value) {
            1 -> R.drawable.jablko
            2 -> R.drawable.swinia
            3 -> R.drawable.
            4 -> R.drawable.
            5 -> R.drawable.
            6 -> R.drawable.
            7 -> R.drawable.
            9 -> R.drawable.
            10 -> R.drawable.
            11 -> R.drawable.
            12 -> R.drawable.
            13 -> R.drawable.
            14 -> R.drawable.
            15 -> R.drawable.
            16 -> R.drawable.
            17 -> R.drawable.
            18 -> R.drawable.
            19 -> R.drawable.
            20 -> R.drawable.
            21 -> R.drawable.
            22 -> R.drawable.
            23 -> R.drawable.
            24 -> R.drawable.
            25 -> R.drawable.
            26 -> R.drawable.
            27 -> R.drawable.
            28 -> R.drawable.
            29 -> R.drawable.
            30 -> R.drawable.
            31 -> R.drawable.
            32 -> R.drawable.
            33 -> R.drawable.
            34 -> R.drawable.
            35 -> R.drawable.
            36 -> R.drawable.
            37 -> R.drawable.
            38 -> R.drawable.
            39 -> R.drawable.
            40 -> R.drawable.
            41 -> R.drawable.
            42 -> R.drawable.
            43 -> R.drawable.
            44 -> R.drawable.
            45 -> R.drawable.
            46 -> R.drawable.
            47 -> R.drawable.
            48 -> R.drawable.
            49 -> R.drawable.
            50 -> R.drawable.
            51 -> R.drawable.
            52 -> R.drawable.
            53 -> R.drawable.
            54 -> R.drawable.
            55 -> R.drawable.
            56 -> R.drawable.
            57 -> R.drawable.
        }
    }*/

}