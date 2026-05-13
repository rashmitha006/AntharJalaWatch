package com.kutira.kushala.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kutira.kushala.R

class ImagePagerAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_pager, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = images[position]
        
        if (imageData.startsWith("base64:")) {
            val base64String = imageData.removePrefix("base64:")
            try {
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                Glide.with(holder.imageView.context)
                    .load(imageBytes)
                    .centerCrop()
                    .into(holder.imageView)
            } catch (e: Exception) {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            Glide.with(holder.imageView.context)
                .load(imageData)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imageView)
        }
    }

    override fun getItemCount() = images.size

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
