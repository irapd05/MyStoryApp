package com.permata.mystoryyapp.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.widget.datawidgetliststory.response.ListStoryItem
import com.google.gson.Gson
import com.permata.mystoryyapp.network.retrofit.ApiConfig
import kotlinx.coroutines.*

class StackRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var storyItems: List<ListStoryItem> = emptyList()

    init {
        setUpAlarmManager()
    }

    @SuppressLint("ShortAlarm")
    private fun setUpAlarmManager() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val interval = 1000L
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent)
    }

    override fun onCreate() {
        Log.d("StackRemoteViewsFactory", "Factory created")
    }

    override fun onDataSetChanged() {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val gson = Gson()

            val token = sharedPreferences.getString("TOKEN", null) ?: return@launch
            val apiService = ApiConfig.getApiService()

            try {
                val response = apiService.getStories("Bearer $token", page = 1, size = 10)

                if (!response.error) {
                    val newStories = response.listStory
                        .map {
                            ListStoryItem(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                photoUrl = it.photoUrl
                            )
                        }

                    storyItems = newStories + storyItems

                    val updatedJson = gson.toJson(storyItems)
                    sharedPreferences.edit().putString("STORY_LIST", updatedJson).apply()

                    val intent = Intent(context, WidgetUpdateReceiver::class.java)
                    context.sendBroadcast(intent)
                } else {
                    storyItems = emptyList()
                }
            } catch (e: Exception) {
                Log.e("StackRemoteViewsFactory", "Gagal mengambil cerita", e)
                storyItems = emptyList()
            }
        }
    }

    override fun onDestroy() {
        Log.d("StackRemoteViewsFactory", "Factory destroyed")
    }

    override fun getCount(): Int {
        return storyItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item)

        if (position < 0 || position >= storyItems.size) {
            Log.e("StackRemoteViewsFactory", "Posisi tidak valid: $position")
            return remoteViews
        }

        val storyItem = storyItems[position]
        remoteViews.setTextViewText(R.id.widget_item_name, storyItem.name)

        try {
            val photoUrl = storyItem.photoUrl
            if (photoUrl.isNotEmpty()) {
                val bitmap = Glide.with(context.applicationContext)
                    .asBitmap()
                    .load(photoUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.baseline_image_24)
                    .submit(300, 300)
                    .get()

                remoteViews.setImageViewBitmap(R.id.widget_item_image, bitmap)
            } else {
                remoteViews.setImageViewResource(R.id.widget_item_image, R.drawable.baseline_image_24)
            }
        } catch (e: Exception) {
            Log.e("StackRemoteViewsFactory", "Gagal memuat gambar untuk posisi $position", e)
            remoteViews.setImageViewResource(R.id.widget_item_image, R.drawable.baseline_image_24)
        }

        val extras = Bundle().apply {
            putString("EXTRA_ID", storyItem.id)
            putString("EXTRA_NAME", storyItem.name)
            putString("EXTRA_DESCRIPTION", storyItem.description)
        }
        val fillInIntent = Intent().apply { putExtras(extras) }
        remoteViews.setOnClickFillInIntent(R.id.widget_item_image, fillInIntent)

        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
