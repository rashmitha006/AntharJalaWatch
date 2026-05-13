package com.kutira.kushala.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kutira.kushala.databinding.ItemProductBinding
import com.kutira.kushala.model.Product

class ProductAdapter(private val onProductClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = emptyList()

    fun submitList(list: List<Product>) {
        products = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvCategory.text = product.category
            binding.tvPrice.text = "₹${product.pricePerUnit} / ${product.unit}"
            binding.tvMinQty.text = "Min: ${product.minOrderQty} ${product.unit}"

            if (product.photoUrls.isNotEmpty()) {
                val photoUrl = product.photoUrls[0]
                if (photoUrl.startsWith("base64:")) {
                    val base64String = photoUrl.removePrefix("base64:")
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    Glide.with(binding.ivProduct.context)
                        .load(imageBytes)
                        .into(binding.ivProduct)
                } else {
                    Glide.with(binding.ivProduct.context)
                        .load(photoUrl)
                        .into(binding.ivProduct)
                }
            } else {
                Glide.with(binding.ivProduct.context)
                    .load(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivProduct)
            }

            binding.root.setOnClickListener { onProductClick(product) }
        }
    }
}