package com.permata.mystoryyapp.di

import android.content.Context
import android.content.SharedPreferences
import com.permata.mystoryyapp.data.StoryRepository
import com.permata.mystoryyapp.database.StoryDatabase
import com.permata.mystoryyapp.network.retrofit.ApiConfig
import com.permata.mystoryyapp.network.retrofit.ApiService

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = provideApiService()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return StoryRepository(database, apiService, sharedPreferences)
    }

    fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }
}
