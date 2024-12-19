package com.permata.mystoryyapp.ui.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.network.response.LocationResponse
import com.permata.mystoryyapp.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val _locationData = MutableLiveData<LocationResponse?>()
    val locationData: LiveData<LocationResponse?> get() = _locationData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchStoriesWithLocation(token: String) {
        _loading.value = true
        val apiService = ApiConfig.getApiService()
        apiService.getLocation("Bearer $token").enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _locationData.value = response.body()
                } else {
                    _errorMessage.value = getApplication<Application>().getString(R.string.error_fetching_data)
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}
