package com.permata.mystoryyapp.ui.addstory

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import com.permata.mystoryyapp.MainActivity
import com.permata.mystoryyapp.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@RunWith(AndroidJUnit4::class)
class AddStoryFragmentTest {

    private lateinit var context: Context

    @JvmField @Mock
    var viewModel: AddStoryViewModel? = null

    @JvmField
    var uploadStatus = MutableLiveData<UploadStatus>()

    @JvmField
    var idlingResource: UploadStatusIdlingResource? = null

    @JvmField
    var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        context = androidx.test.core.app.ApplicationProvider.getApplicationContext()

        uploadStatus = MutableLiveData()

        idlingResource = UploadStatusIdlingResource(uploadStatus)
        IdlingRegistry.getInstance().register(idlingResource)

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    suspend fun testUploadStory() {
        onView(withId(R.id.galleryButton)).perform(click())

        onView(withId(R.id.progressIndicator)).check(matches(isDisplayed()))

        onView(withId(R.id.storyEditText)).perform(typeText("This is a new story"))
        onView(withId(R.id.uploadButton)).perform(click())

        uploadStatus.postValue(UploadStatus.Loading)

        onView(withId(R.id.progressIndicator)).check(matches(isDisplayed()))

        uploadStatus.postValue(UploadStatus.Success("Upload Successful"))

        onView(withText("Upload Successful")).check(matches(isDisplayed()))

        val file = mock(File::class.java)
        val latRequestBody = "latitude_value".toRequestBody("text/plain".toMediaType())
        val lonRequestBody = "longitude_value".toRequestBody("text/plain".toMediaType())
        val token = "auth_token"
        val description = "This is a new story"

        verify(viewModel)?.uploadStory(file, description, token, latRequestBody, lonRequestBody)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)

        scenario?.close()
    }
}
