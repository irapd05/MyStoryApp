package com.permata.mystoryyapp.ui.authentication.signup

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = SignupViewModelFactory()
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        binding.signupButton.isEnabled = false

        setupFormValidation()

        binding.signupButton.setOnClickListener {
            handleSignup()
        }
    }

    private fun handleSignup() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        binding.nameEditTextLayout.error = null
        binding.emailEditTextLayout.error = null
        binding.passwordEditTextLayout.error = null

        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.name_empty_error)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.email_empty_error)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditTextLayout.error = getString(R.string.email_invalid_error)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.password_empty_error)
            isValid = false
        }

        if (!isValid) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.signupButton.isEnabled = false

        signupViewModel.registerUser(name, email, password).observe(this) { response ->
            binding.progressBar.visibility = View.GONE
            binding.signupButton.isEnabled = true
            if (response.error) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupFormValidation() {
        val validateForm = {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (name.isEmpty()) {
                binding.nameEditTextLayout.error = getString(R.string.name_empty_error)
            } else {
                binding.nameEditTextLayout.error = null
            }

            if (email.isEmpty()) {
                binding.emailEditTextLayout.error = getString(R.string.email_empty_error)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditTextLayout.error = getString(R.string.email_invalid_error)
            } else {
                binding.emailEditTextLayout.error = null
            }

            if (password.isEmpty()) {
                binding.passwordEditTextLayout.error = getString(R.string.password_empty_error)
            } else {
                binding.passwordEditTextLayout.error = null
            }

            val isFormValid = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

            binding.signupButton.isEnabled = isFormValid
        }

        binding.nameEditText.addTextChangedListener { validateForm() }
        binding.emailEditText.addTextChangedListener { validateForm() }
        binding.passwordEditText.addTextChangedListener { validateForm() }
    }
}
