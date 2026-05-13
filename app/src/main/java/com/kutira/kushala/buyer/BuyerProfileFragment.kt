package com.kutira.kushala.buyer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kutira.kushala.SplashActivity
import com.kutira.kushala.databinding.FragmentBuyerProfileBinding
import com.kutira.kushala.viewmodel.AuthViewModel

class BuyerProfileFragment : Fragment() {
    private var _binding: FragmentBuyerProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnSaveBuyerProfile.setOnClickListener {
            Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogoutBuyer.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}