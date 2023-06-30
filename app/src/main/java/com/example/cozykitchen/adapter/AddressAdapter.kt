package com.example.cozykitchen.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.ui.fragment.OnAddressClickListener

class AddressAdapter(private val addresses: List<Address>, private val listener: OnAddressClickListener): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvStreet: TextView = itemView.findViewById(R.id.tv_address)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.image_button_delete)
        private val cardAddress: CardView = itemView.findViewById(R.id.card_address)

        init {
            btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = getAddressObject(position)
                    listener.onDeleteClick(address.addressId)
                }
            }
        }

        init {
            cardAddress.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = getAddressObject(position)
                    listener.onCardClick(address)
                }
            }
        }

        private fun getAddressObject(position: Int): Address {
            return addresses[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.AddressViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
        val currentAddress = addresses[position]

        holder.tvStreet.text = currentAddress.name
    }
}