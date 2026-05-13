package com.kutira.kushala.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import com.kutira.kushala.R
import com.kutira.kushala.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem(
                "Showcase Your Skill",
                "Create a digital profile for your cottage industry and reach bulk buyers across the country.",
                R.drawable.ic_launcher_foreground // Replace with actual illustrations
            ),
            OnboardingItem(
                "Manage Capacity",
                "Update your production capacity in real-time so buyers know when you are ready for orders.",
                R.drawable.ic_launcher_foreground
            ),
            OnboardingItem(
                "Direct Connection",
                "Chat on WhatsApp or call buyers directly to finalize your business deals without middlemen.",
                R.drawable.ic_launcher_foreground
            )
        )

        val adapter = OnboardingAdapter(items)
        binding.viewPagerOnboarding.adapter = adapter
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerOnboarding) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            if (binding.viewPagerOnboarding.currentItem < items.size - 1) {
                binding.viewPagerOnboarding.currentItem += 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    data class OnboardingItem(val title: String, val description: String, val image: Int)

    inner class OnboardingAdapter(private val items: List<OnboardingItem>) :
        RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
            return OnboardingViewHolder(view)
        }

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.description.text = item.description
            holder.image.setImageResource(item.image)
        }

        override fun getItemCount() = items.size

        inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvTitle)
            val description: TextView = view.findViewById(R.id.tvDescription)
            val image: ImageView = view.findViewById(R.id.ivOnboarding)
        }
    }
}