package com.permata.mystoryyapp.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.permata.mystoryyapp.network.response.LoginResponse
import com.permata.mystoryyapp.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    fun loginUser(email: String, password: String): LiveData<LoginResponse> = liveData(Dispatchers.IO) {
        val request = LoginRequest(email, password)
        try {
            val response = apiService.loginUser(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(body)
                } else {
                    emit(LoginResponse(error = true, message = "Empty response", loginResult = null))
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "User not found. Please register first."
                    401 -> "Incorrect email or password."
                    else -> "Error: ${response.message()}"
                }
                emit(LoginResponse(error = true, message = errorMessage, loginResult = null))
            }
        } catch (e: Exception) {
            emit(LoginResponse(error = true, message = "Exception: ${e.message}", loginResult = null))
        }
    }
}
