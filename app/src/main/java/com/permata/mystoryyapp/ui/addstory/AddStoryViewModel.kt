package com.permata.mystoryyapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.permata.mystoryyapp.di.Injection
import com.permata.mystoryyapp.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class UploadStatus {
    data object Loading : UploadStatus()
    data class Success(val message: String) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}

class AddStoryViewModel : ViewModel() {
    private val apiService: ApiService = Injection.provideApiService()

    private val _uploadStatus = MutableLiveData<UploadStatus>()
    val uploadStatus: LiveData<UploadStatus> = _uploadStatus

    suspend fun uploadStory(
        image: File,
        description: String,
        token: String?,
        latRequestBody: RequestBody?,
        lonRequestBody: RequestBody?
    ) {
        if (token == null) {
            _uploadStatus.postValue(UploadStatus.Error("No token found"))
            return
        }

        val requestImageFile = image.asRequestBody("image/jpg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", image.name, requestImageFile)
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        _uploadStatus.postValue(UploadStatus.Loading)

        withContext(Dispatchers.IO) {
            try {
                val response = apiService.addStory(
                    "Bearer $token",
                    multipartBody,
                    descriptionBody,
                    latRequestBody,
                    lonRequestBody
                )
                _uploadStatus.postValue(UploadStatus.Success(response.message.toString()))
            } catch (e: Exception) {
                _uploadStatus.postValue(UploadStatus.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }
}