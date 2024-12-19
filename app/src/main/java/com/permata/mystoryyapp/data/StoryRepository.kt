package com.permata.mystoryyapp.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.permata.mystoryyapp.database.StoryDatabase
import com.permata.mystoryyapp.network.retrofit.ApiService
import com.permata.mystoryyapp.network.response.ListStoryItem
import com.permata.mystoryyapp.ui.listaddstory.utamaliststory.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences
) {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, sharedPreferences),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun fetchStories(
        token: String,
        loadingStatus: MutableLiveData<Boolean>,
        errorStatus: MutableLiveData<String?>,
        stories: MutableLiveData<Resource<List<ListStoryItem>>>
    ) {
        loadingStatus.postValue(true)
        stories.postValue(Resource.Loading())
        errorStatus.postValue(null)

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getStories("Bearer $token")
                if (response.error) {
                    errorStatus.postValue("Error: ${response.message}")
                    stories.postValue(Resource.Error("Error: ${response.message}"))
                } else {
                    stories.postValue(Resource.Success(response.listStory))
                }
            }
        } catch (e: Exception) {
            loadingStatus.postValue(false)
            errorStatus.postValue("Failure: ${e.message}")
            stories.postValue(Resource.Error("Failure: ${e.message}"))
        } finally {
            loadingStatus.postValue(false)
        }
    }
}
