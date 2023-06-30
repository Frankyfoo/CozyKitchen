package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Card
import com.example.cozykitchen.ui.fragment.OnCardClickListener

class CardAdapter(private val cards: List<Card>, private val listener: OnCardClickListener? = null ): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCardNumber: TextView = itemView.findViewById(R.id.tv_card_number)
        val tvExpiryDate: TextView = itemView.findViewById(R.id.tv_expiry_date)
        val tvCvcCode: TextView = itemView.findViewById(R.id.tv_cvc_code)
        private val btnDeleteCard: ImageButton = itemView.findViewById(R.id.image_button_delete)

        init {
            btnDeleteCard.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val card = getCardObject(position)
                    listener?.onDeleteClick(card.cardId!!)
                }
            }
        }

        private fun getCardObject(position: Int): Card {
            return cards[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentCard = cards[position]

        holder.tvCardNumber.text = "Card Number: " + formatCardNumber(currentCard.cardNumber!!)
        holder.tvExpiryDate.text = "Card Expiry Date: " + currentCard.cardExpiryDateString
        holder.tvCvcCode.text = "CVC Code: " + currentCard.cvcCode
    }

    private fun formatCardNumber(cardNumber: String): String {
        if (cardNumber.isNullOrEmpty()) {
            return cardNumber
        }

        val result = StringBuilder()
        for (i in cardNumber.indices) {
            if (i > 0 && i % 4 == 0) {
                result.append(" ")
            }
            result.append(cardNumber[i])
        }

        return result.toString()
    }
}