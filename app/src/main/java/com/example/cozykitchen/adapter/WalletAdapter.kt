package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.model.Wallet
import com.example.cozykitchen.ui.fragment.OnWalletClickListener

class WalletAdapter(private val wallets: List<Wallet>, private val listener: OnWalletClickListener): RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    inner class WalletViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tv_address)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.image_button_delete)

        init {
            btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val wallet = getWalletObject(position)
                    listener.onDeleteClick(wallet.walletId)
                }
            }
        }

        private fun getWalletObject(position: Int): Wallet {
            return wallets[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletAdapter.WalletViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return WalletViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return wallets.size
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val currentWallet = wallets[position]

        holder.tvPhoneNumber.text = currentWallet.phoneNumber
    }
}