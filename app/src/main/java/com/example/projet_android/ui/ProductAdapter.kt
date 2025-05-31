package com.example.projet_android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.projet_android.R
import com.example.projet_android.model.Product

class ProductAdapter(
    private var productList: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var lastPosition = -1

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.imageProduct)
        val productTitle: TextView = itemView.findViewById(R.id.titleProduct)
        val productPrice: TextView = itemView.findViewById(R.id.priceProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productTitle.text = product.title
        holder.productPrice.text = "${product.price} €"
        
        // Charger l'image avec une transition
        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.placeholder_image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.productImage)

        // Animer la vue si elle n'a pas déjà été animée
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.slide_in_right
            )
            animation.duration = 300
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }

        holder.itemView.setOnClickListener {
            onClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        val diffCallback = ProductDiffCallback(productList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        productList = newList
        diffResult.dispatchUpdatesTo(this)
        
        // Réinitialiser la position d'animation si c'est une nouvelle liste
        lastPosition = -1
    }
    
    // Classe pour calculer les différences entre deux listes
    private class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int = oldList.size
        
        override fun getNewListSize(): Int = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
