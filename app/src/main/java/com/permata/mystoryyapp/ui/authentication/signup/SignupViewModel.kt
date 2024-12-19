package com.permata.mystoryyapp.ui.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.permata.mystoryyapp.network.response.ResponseSignup
import com.permata.mystoryyapp.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers

class SignupViewModel(private val apiService: ApiService) : ViewModel() {

    fun registerUser(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        val request = SignupRequest(name, email, password)
        try {
            val response = apiService.registerUser(request)
            if (response.isSuccessful) {
                emit(response.body() ?: ResponseSignup(error = true, message = "Invalid response"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Email sudah terdaftar"
                    else -> "Error: ${response.message()}"
                }
                emit(ResponseSignup(error = true, message = errorMessage))
            }
        } catch (e: Exception) {
            emit(ResponseSignup(error = true, message = "Error: ${e.message}"))
        }
    }
}
