package com.permata.mystoryyapp.ui.authentication.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.permata.mystoryyapp.MainActivity
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.ActivityLoginBinding
import com.permata.mystoryyapp.ui.authentication.signup.SignupActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        if (isUserLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = LoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setupFormValidation()

        binding.loginButton.setOnClickListener {
            handleLogin()
        }

        setupSignUpLink()
    }

    private fun setupFormValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        binding.loginButton.isEnabled = false
    }

    private fun validateForm() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        var isFormValid = true

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.email_empty_error)
            isFormValid = false
        } else if (!isValidEmail(email)) {
            binding.emailEditTextLayout.error = getString(R.string.email_invalid_error)
            isFormValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.password_empty_error)
            isFormValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        binding.loginButton.isEnabled = isFormValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        binding.emailEditTextLayout.error = null
        binding.passwordEditTextLayout.error = null

        if (email.isEmpty() || !isValidEmail(email) || password.isEmpty()) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        binding.loginButton.isEnabled = false

        loginViewModel.loginUser(email, password).observe(this) { response ->
            binding.progressBar.visibility = View.GONE

            binding.loginButton.isEnabled = true

            if (response.error) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            } else {
                val loginResult = response.loginResult
                if (loginResult != null) {
                    saveLoginSession(loginResult.name, loginResult.token)
                    navigateToMain()
                } else {
                    Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    private fun saveLoginSession(userName: String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.putString("USER_NAME", userName)
        editor.putString("TOKEN", token)
        editor.apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupSignUpLink() {
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
