package com.example.cozykitchen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.ui.fragment.OnCartClickListener

class CartAdapter(private val shoppingCarts: List<ShoppingCart>, private val listener: OnCartClickListener? = null): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cartImage: ImageView = itemView.findViewById(R.id.cart_image)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val productSize: TextView = itemView.findViewById(R.id.tv_product_size)
        val productQuantity: TextView = itemView.findViewById(R.id.tv_product_quantity)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        val productDescription: TextView = itemView.findViewById(R.id.tv_cart_description)
        val productIndividualPrice: TextView = itemView.findViewById(R.id.tv_product_individual_price)
        val btnRemoveFromCart: Button = itemView.findViewById(R.id.btn_remove_from_cart)

        init {
            btnRemoveFromCart.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val shoppingCart = getShoppingCartObject(position)
                    listener?.onRemoveButtonClick(shoppingCart.shoppingCartId)
                }
            }
        }

        private fun getShoppingCartObject(position: Int): ShoppingCart {
            return shoppingCarts[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return shoppingCarts.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentCart = shoppingCarts[position]

        if (currentCart.product != null) {
            holder.productName.text = currentCart.product.productName
            holder.productSize.text = "Portion: "  + currentCart.size
            holder.productQuantity.text = "Qty: ${currentCart.quantityBought}"
//            holder.productIndividualPrice.text = "Price: ${currentCart.product.productPrice}"

            if (!currentCart.orderId.isNullOrEmpty() || currentCart.status == "PAY_COMPLETED") {
                holder.btnRemoveFromCart.visibility = View.GONE
            } else {
                holder.btnRemoveFromCart.visibility = View.VISIBLE
            }

            if (currentCart.size == "Large") {
                holder.productPrice.text = "Total: RM ${String.format("%.2f", (currentCart.product.productPrice + 2.00f) * currentCart.quantityBought)}"
                holder.productIndividualPrice.text = "Price: ${String.format("%.2f", (currentCart.product.productPrice + 2.00f))}"

            } else {
                holder.productPrice.text = "Total: RM ${String.format("%.2f", (currentCart.product.productPrice) * currentCart.quantityBought)}"
                holder.productIndividualPrice.text = "Price: ${String.format("%.2f", currentCart.product.productPrice)}"
            }

            if (currentCart.customizationDescription.isNullOrEmpty()) {
                holder.productDescription.text = "-"
            } else {
                holder.productDescription.text = currentCart.customizationDescription
            }

            if(currentCart.product.productUrl.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.no_image)
                    .into(holder.cartImage)
            } else {
                Glide.with(holder.itemView.context)
                    .load(currentCart.product.productUrl)
                    .into(holder.cartImage)
            }
        }
    }
}