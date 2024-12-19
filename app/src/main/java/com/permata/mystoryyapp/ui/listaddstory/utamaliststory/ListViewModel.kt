package com.permata.mystoryyapp.ui.listaddstory.utamaliststory

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.permata.mystoryyapp.data.StoryRepository
import com.permata.mystoryyapp.network.response.ListStoryItem
import kotlinx.coroutines.launch

class ListViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories().cachedIn(viewModelScope)

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> = _loadingStatus

    private val _errorStatus = MutableLiveData<String?>()
    val errorStatus: LiveData<String?> = _errorStatus

    private val _stories = MutableLiveData<Resource<List<ListStoryItem>>>()
    val stories: LiveData<Resource<List<ListStoryItem>>> = _stories

    fun fetchStories(token: String) {
        viewModelScope.launch {
            storyRepository.fetchStories(token, _loadingStatus, _errorStatus, _stories)
        }
    }
}
