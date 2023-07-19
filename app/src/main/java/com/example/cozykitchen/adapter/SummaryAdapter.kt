package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Summary

class SummaryAdapter(private val summaries: List<Summary>): RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.summary_image)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        val tvProductSize: TextView = itemView.findViewById(R.id.tv_product_size)
        val tvProductQuantity: TextView = itemView.findViewById(R.id.tv_product_quantity)
        val tvOrder: TextView = itemView.findViewById(R.id.tv_product_order)
        val tvProductDescription: TextView = itemView.findViewById(R.id.tv_product_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_summary, parent, false)
        return SummaryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return summaries.size
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val currentItem = summaries[position]

        holder.tvProductName.text = currentItem.productName
        holder.tvProductQuantity.text = "Qty: ${currentItem.quantityBought}"
        holder.tvProductSize.text = currentItem.size
        holder.tvOrder.text = "From: ${currentItem.orderId}"

        if (currentItem.customizationDescription.isNullOrEmpty()) {
            holder.tvProductDescription.text = "-"
        } else {
            holder.tvProductDescription.text = currentItem.customizationDescription
        }

        if (currentItem.productUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.no_image)
                .into(holder.imgProduct)
        } else {
            Glide.with(holder.itemView.context)
                .load(currentItem.productUrl)
                .into(holder.imgProduct)
        }
    }

}