package com.permata.mystoryyapp.network.retrofit

import com.permata.mystoryyapp.network.response.AddNewStoryResponse
import com.permata.mystoryyapp.network.response.ListDetailResponse
import com.permata.mystoryyapp.network.response.LocationResponse
import com.permata.mystoryyapp.network.response.LoginResponse
import com.permata.mystoryyapp.network.response.ResponseSignup
import com.permata.mystoryyapp.ui.authentication.login.LoginRequest
import com.permata.mystoryyapp.ui.authentication.signup.SignupRequest
import com.permata.mystoryyapp.network.response.ListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): AddNewStoryResponse


    @POST("login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun registerUser(
        @Body request: SignupRequest
    ): Response<ResponseSignup>

    @GET("stories/{id}")
    fun getStoriesDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Call<ListDetailResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0
    ): ListResponse

    @GET("stories?location=1")
    fun getLocation(
        @Header("Authorization") token: String
    ): Call<LocationResponse>
}
