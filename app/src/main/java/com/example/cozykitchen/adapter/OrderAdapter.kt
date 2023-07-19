package com.example.cozykitchen.adapter

import android.graphics.Color
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

class OrderAdapter(private var orders: List<Order>, private val listener: OnOrderClickListener): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_title)
        val tvOrderPrice: TextView = itemView.findViewById(R.id.tv_order_price)
        val tvRemarks: TextView = itemView.findViewById(R.id.tv_order_remarks)
        val tvPaymentType: TextView = itemView.findViewById(R.id.tv_order_payment_type)
        val tvDeliveryTime: TextView = itemView.findViewById(R.id.tv_order_delivery_time)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tv_order_status)
        val cardOrder: CardView = itemView.findViewById(R.id.cardOrder)

        init {
            cardOrder.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val order = getOrderObject(position)
                    listener.onCardClick(order)
                }
            }
        }

        private fun getOrderObject(position: Int): Order {
            return orders[position]
        }
    }

    // Function to update the order list
    fun updateOrderList(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }

//    fun clearOrderList() {
//        orders.clear()
//        notifyDataSetChanged()
//    }

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
        if(currentOrder.remarks.isNullOrEmpty()) {
            holder.tvRemarks.text = "Remarks: Empty"
        } else {
            holder.tvRemarks.text = "Remarks: " + currentOrder.remarks
        }
        holder.tvPaymentType.text = "Paid with: " + currentOrder.paymentType
        holder.tvDeliveryTime.text = "Delivery Time: " + currentOrder.deliveryDateTimeString
        holder.tvOrderPrice.text = "Total Cost: RM " + (currentOrder.totalPrice + 3.0f).toString()

        when (currentOrder.status) {
            "PAY_COMPLETED" -> {
                holder.tvOrderStatus.setTextColor(Color.BLUE)
                holder.tvOrderStatus.text = "Status: Paid"
            }
            "CANCELLED" -> {
                holder.tvOrderStatus.setTextColor(Color.RED)
                holder.tvOrderStatus.text = "Status: Order cancelled"
            }
            else -> {
                holder.tvOrderStatus.setTextColor(Color.GREEN)
                holder.tvOrderStatus.text = "Status: Order Delivered"
            }
        }
    }
}