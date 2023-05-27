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
import com.example.cozykitchen.model.Product

class ProductAdapter(private val products: List<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val foodCard: CardView = itemView.findViewById(R.id.cardFood)
        val foodImage: ImageView = itemView.findViewById(R.id.imageFood)
        val foodName: TextView = itemView.findViewById(R.id.TvFoodName)
        val foodPrice: TextView = itemView.findViewById(R.id.TvFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]

        holder.foodName.text = currentProduct.productName
        holder.foodPrice.text = "RM " + currentProduct.productPrice.toString()

        // if no image url is empty, show no image jpg
        if(currentProduct.productImageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.no_image)
                .into(holder.foodImage)
        } else {
            Glide.with(holder.itemView.context)
                .load(currentProduct.productImageUrl)
                .into(holder.foodImage)
        }
    }

}