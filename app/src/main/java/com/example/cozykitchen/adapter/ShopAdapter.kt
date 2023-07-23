package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.ui.fragment.OnShopClickListener

class ShopAdapter(private var shops: List<Shop>, private val listener: OnShopClickListener): RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shopCard: CardView = itemView.findViewById(R.id.cardShop)
        val shopImage: ImageView = itemView.findViewById(R.id.imageShop)
        val shopName: TextView = itemView.findViewById(R.id.tvShopName)
        val shopDistance: TextView = itemView.findViewById(R.id.tv_distance)

        init {
            shopCard.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val shop = getShopObject(position)
                    listener.onItemClick(shop)
                }
            }
        }

        private fun getShopObject(position: Int): Shop {
            return shops[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return shops.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val currentShop = shops[position]

        // Bind data to views
        holder.shopName.text = currentShop.shopName
        holder.shopDistance.text = "Distance: ${String.format("%.1f", currentShop.distance)} Km from your location."

        // if no image url is empty, show no image jpg
        if(currentShop.shopImageUrl.isEmpty()) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.no_image)
                .into(holder.shopImage)
        } else {
            Glide.with(holder.itemView.context)
                .load(currentShop.shopImageUrl)
                .into(holder.shopImage)
        }
    }

    // update shop list based on search query
    fun updateShopList(newShopList: List<Shop>, searchQuery: String) {
        if (searchQuery.isEmpty()) {
            // If search query is empty, display all shops
            shops = newShopList
        } else {
            // Filter shops based on search query
            shops = newShopList.filter { shop ->
                shop.shopName.contains(searchQuery, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}