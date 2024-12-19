package com.permata.mystoryyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.permata.mystoryyapp.MainActivity
import com.permata.mystoryyapp.R

@Suppress("DEPRECATION")
class ListStoryAppWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.permata.mystoryyapp.TOAST_ACTION"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.list_story_app_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)

                val toastIntent = Intent(context, ListStoryAppWidget::class.java).apply {
                    action = TOAST_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }

                val toastPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
        }
    }


        override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == TOAST_ACTION) {
            val storyId = intent.getStringExtra("EXTRA_ID")
            val storyName = intent.getStringExtra("EXTRA_NAME")
            val storyDescription = intent.getStringExtra("EXTRA_DESCRIPTION")

            val mainIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("EXTRA_ID", storyId)
                putExtra("EXTRA_NAME", storyName)
                putExtra("EXTRA_DESCRIPTION", storyDescription)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(mainIntent)
        }
    }

}
