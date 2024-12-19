package com.permata.mystoryyapp.ui.profile

import ProfileResponse
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class ProfileViewModel : ViewModel() {
    fun getUserProfile(sharedPreferences: SharedPreferences): LiveData<ProfileResponse> = liveData(Dispatchers.IO) {
        try {
            val userName = sharedPreferences.getString("USER_NAME", "Unknown User") ?: "Unknown User"
            emit(ProfileResponse(error = false, message = "Success", userName = userName))
        } catch (e: Exception) {
            emit(ProfileResponse(error = true, message = "Exception: ${e.message}", userName = null))
        }
    }
}
