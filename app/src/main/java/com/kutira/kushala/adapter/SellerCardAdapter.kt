package com.kutira.kushala.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kutira.kushala.R
import com.kutira.kushala.databinding.ItemSellerCardBinding
import com.kutira.kushala.model.Seller

class SellerCardAdapter(private val onSellerClick: (Seller) -> Unit) :
    RecyclerView.Adapter<SellerCardAdapter.SellerViewHolder>() {

    private var sellers: List<Seller> = emptyList()

    fun submitList(list: List<Seller>) {
        sellers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val binding = ItemSellerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        holder.bind(sellers[position])
    }

    override fun getItemCount() = sellers.size

    inner class SellerViewHolder(private val binding: ItemSellerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(seller: Seller) {
            binding.tvBusinessName.text = seller.businessName
            binding.tvSkillArea.text = seller.skillArea
            binding.tvLocation.text = "${seller.village}, ${seller.district}"
            binding.tvRating.text = "⭐ ${seller.avgRating}"
            binding.tvCapacity.text = "${seller.unitsAvailableThisWeek} units available"

            if (seller.isAvailable) {
                binding.tvStatusBadge.text = "Taking Orders"
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_green)
            } else {
                binding.tvStatusBadge.text = "Busy/Closed"
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_red)
            }

            if (seller.teamPhotoUrl.isNotEmpty()) {
                val photoUrl = seller.teamPhotoUrl
                if (photoUrl.startsWith("base64:")) {
                    val base64String = photoUrl.removePrefix("base64:")
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    Glide.with(binding.ivSellerPhoto.context)
                        .load(imageBytes)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(binding.ivSellerPhoto)
                } else {
                    Glide.with(binding.ivSellerPhoto.context)
                        .load(photoUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(binding.ivSellerPhoto)
                }
            } else {
                Glide.with(binding.ivSellerPhoto.context)
                    .load(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivSellerPhoto)
            }

            binding.root.setOnClickListener { onSellerClick(seller) }
        }
    }
}