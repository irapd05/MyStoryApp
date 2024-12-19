package com.permata.mystoryyapp.ui.listaddstory.detailliststory

import android.app.Application
import androidx.lifecycle.*
import com.permata.mystoryyapp.network.response.ListDetailResponse
import com.permata.mystoryyapp.network.response.Story
import com.permata.mystoryyapp.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailListStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getStoryDetail(token: String, storyId: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getStoriesDetail(token, storyId)
        client.enqueue(object : Callback<ListDetailResponse> {
            override fun onResponse(call: Call<ListDetailResponse>, response: Response<ListDetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _story.value = responseBody.story
                    } else {
                        _errorMessage.value = "Story not found in the response"
                    }
                } else {
                    _errorMessage.value = "Failed to fetch story details: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ListDetailResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to connect: ${t.message}"
            }
        })
    }
}

