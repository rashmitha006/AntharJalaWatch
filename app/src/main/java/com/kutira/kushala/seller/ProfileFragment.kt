package com.kutira.kushala.seller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.kutira.kushala.SplashActivity
import com.kutira.kushala.databinding.FragmentSellerProfileBinding
import com.kutira.kushala.utils.LocaleHelper
import com.kutira.kushala.viewmodel.AuthViewModel
import com.kutira.kushala.viewmodel.SellerViewModel
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentSellerProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SellerViewModel
    private lateinit var authViewModel: AuthViewModel
    private var selectedImageUri: Uri? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.ivTeamPhoto.setImageURI(selectedImageUri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SellerViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupLanguageToggle()

        viewModel.seller.observe(viewLifecycleOwner) { seller ->
            seller?.let {
                binding.etBusinessName.setText(it.businessName)
                binding.etSkillArea.setText(it.skillArea)
                binding.sliderExperience.value = it.yearsExperience.toFloat().coerceIn(1f, 30f)
                binding.etDescription.setText(it.description)
                binding.switchShowContact.isChecked = it.showContact
                
                if (it.teamPhotoUrl.isNotEmpty()) {
                    if (it.teamPhotoUrl.startsWith("base64:")) {
                        val base64String = it.teamPhotoUrl.removePrefix("base64:")
                        val imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                        Glide.with(this).load(imageBytes).into(binding.ivTeamPhoto)
                    } else {
                        Glide.with(this).load(it.teamPhotoUrl).into(binding.ivTeamPhoto)
                    }
                }
            }
        }

        binding.fabEditPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(600, 600)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.btnSaveProfile.setOnClickListener {
            val currentSeller = viewModel.seller.value ?: return@setOnClickListener
            
            binding.btnSaveProfile.isEnabled = false
            binding.btnSaveProfile.text = "Saving..."

            val updatedSeller = currentSeller.copy(
                businessName = binding.etBusinessName.text.toString(),
                skillArea = binding.etSkillArea.text.toString(),
                yearsExperience = binding.sliderExperience.value.toInt(),
                description = binding.etDescription.text.toString(),
                showContact = binding.switchShowContact.isChecked,
                updatedAt = System.currentTimeMillis()
            )
            
            // Handle profile photo update via Base64 workaround
            if (selectedImageUri != null) {
                val base64Image = com.kutira.kushala.repository.ProductRepository().convertImageToBase64(requireContext(), selectedImageUri!!)
                if (base64Image != null) {
                    viewModel.updateCapacity(updatedSeller.copy(teamPhotoUrl = base64Image))
                } else {
                    viewModel.updateCapacity(updatedSeller)
                }
            } else {
                viewModel.updateCapacity(updatedSeller)
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            status?.let { (success, _) ->
                if (success) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    binding.btnSaveProfile.isEnabled = true
                    binding.btnSaveProfile.text = "Save Profile"
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupLanguageToggle() {
        val currentLang = Locale.getDefault().language
        if (currentLang == "kn") {
            binding.toggleLanguage.check(binding.btnLangKannada.id)
        } else {
            binding.toggleLanguage.check(binding.btnLangEnglish.id)
        }

        binding.toggleLanguage.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val lang = if (checkedId == binding.btnLangKannada.id) "kn" else "en"
                if (lang != currentLang) {
                    changeLanguage(lang)
                }
            }
        }
    }

    private fun changeLanguage(lang: String) {
        LocaleHelper.setLocale(requireContext(), lang)
        val intent = Intent(requireContext(), SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
