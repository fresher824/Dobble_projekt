package com.example.dobble_projekt

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.dobble_projekt.databinding.FragmentDeviceItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyDeviceListRecyclerViewAdapter(
    private val names: MutableList<String>,
    private val addresses: MutableList<String>
) : RecyclerView.Adapter<MyDeviceListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDeviceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val names = names[position]
        val addresses = addresses[position]
        holder.adressDevice.text = addresses
        holder.nameDevice.text = names
    }

    override fun getItemCount(): Int = names.size

    inner class ViewHolder(binding: FragmentDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val adressDevice: TextView = binding.nameDevice
        val nameDevice: TextView = binding.adressDevice

        override fun toString(): String {
            return super.toString() + " '" + nameDevice.text + "'"
        }
    }

}