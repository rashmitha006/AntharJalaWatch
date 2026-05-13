package com.kutira.kushala.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kutira.kushala.databinding.ItemInquiryBinding
import com.kutira.kushala.model.Inquiry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InquiryAdapter(private val onInquiryClick: (Inquiry) -> Unit) :
    RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder>() {

    private var inquiries: List<Inquiry> = emptyList()

    fun submitList(list: List<Inquiry>) {
        inquiries = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InquiryViewHolder {
        val binding = ItemInquiryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InquiryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InquiryViewHolder, position: Int) {
        holder.bind(inquiries[position])
    }

    override fun getItemCount() = inquiries.size

    inner class InquiryViewHolder(private val binding: ItemInquiryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(inquiry: Inquiry) {
            binding.tvBuyerName.text = inquiry.buyerName
            binding.tvProductName.text = "Interested in: ${inquiry.productInterest}"
            
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            binding.tvTimestamp.text = sdf.format(Date(inquiry.timestamp))

            binding.root.setOnClickListener { onInquiryClick(inquiry) }
        }
    }
}