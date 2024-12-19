package com.permata.mystoryyapp.ui.addstory

import androidx.test.espresso.IdlingResource
import androidx.lifecycle.LiveData

class UploadStatusIdlingResource(private val liveData: LiveData<UploadStatus>) : IdlingResource {
    private var callback: IdlingResource.ResourceCallback? = null

    init {
        liveData.observeForever {
            when (it) {
                is UploadStatus.Loading -> {
                    callback?.onTransitionToIdle()
                }

                is UploadStatus.Success, is UploadStatus.Error -> {
                    callback?.onTransitionToIdle()
                }
            }
        }
    }

    override fun getName(): String = "UploadStatusIdlingResource"

    override fun isIdleNow(): Boolean {
        return liveData.value !is UploadStatus.Loading
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}