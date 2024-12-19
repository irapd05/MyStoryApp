package com.permata.mystoryyapp.ui.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.permata.mystoryyapp.di.Injection

class SignupViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            val apiService = Injection.provideApiService()
            @Suppress("UNCHECKED_CAST")
            return SignupViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
