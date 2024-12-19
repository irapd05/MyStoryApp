@file:Suppress("DEPRECATION")

package com.permata.mystoryyapp.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.FragmentProfileBinding
import com.permata.mystoryyapp.ui.authentication.welcome.WelcomeActivity
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserProfile()
        setupLanguageSpinner()
        setupDarkModeSwitch()

        binding.logoutTitle.setOnClickListener { handleLogout() }
    }

    private fun fetchUserProfile() {
        profileViewModel.getUserProfile(sharedPreferences).observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse.error) {
                Toast.makeText(requireContext(), profileResponse.message, Toast.LENGTH_SHORT).show()
            } else {
                binding.userName.text = profileResponse.userName ?: getString(R.string.name)
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = listOf(getString(R.string.english), getString(R.string.indonesia))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        val currentLanguage = sharedPreferences.getString("APP_LANGUAGE", "en")
        binding.languageSpinner.setSelection(if (currentLanguage == "en") 0 else 1)

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = if (position == 0) "en" else "id"
                if (selectedLanguage != currentLanguage) {
                    setLocale(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setLocale(languageCode: String) {

        sharedPreferences.edit().putString("APP_LANGUAGE", languageCode).apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        requireActivity().recreate()
    }

    private fun setupDarkModeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("IS_DARK_MODE", false)
        binding.darkModeSwitch.isChecked = isDarkMode
        Log.d("ProfileFragment", "Dark mode enabled: $isDarkMode")

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("IS_DARK_MODE", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            requireActivity().recreate()
        }
    }

    private fun handleLogout() {
        sharedPreferences.edit().clear().apply()

        Toast.makeText(requireContext(), getString(R.string.logout_successful), Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
