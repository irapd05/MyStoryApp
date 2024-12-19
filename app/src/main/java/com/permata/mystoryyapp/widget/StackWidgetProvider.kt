package com.permata.mystoryyapp.widget

import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.permata.mystoryyapp.R

@Suppress("DEPRECATION")
class StackWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.list_story_app_widget)

            // Setup RemoteViews to point to StackRemoteViewsFactory
            val intent = Intent(context, StackWidgetService::class.java)
            views.setRemoteAdapter(R.id.stack_view, intent)

            // Setup the empty view
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
