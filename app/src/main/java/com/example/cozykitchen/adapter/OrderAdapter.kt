package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Card
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.ui.fragment.OnOrderClickListener

class OrderAdapter(private val orders: List<Order>, private val listener: OnOrderClickListener): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_title)
        val tvOrderPrice: TextView = itemView.findViewById(R.id.tv_order_price)
        val cardOrder: CardView = itemView.findViewById(R.id.cardOrder)

        init {
            cardOrder.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val orderId = getOrderId(position)
                    listener.onCardClick(orderId)
                }
            }
        }

        private fun getOrderId(position: Int): String {
            return orders[position].orderId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderAdapter.OrderViewHolder, position: Int) {
        val currentOrder = orders[position]

        holder.tvOrderId.text = currentOrder.orderId
        holder.tvOrderPrice.text = "Total Cost: RM " + currentOrder.totalPrice.toString()
    }
}