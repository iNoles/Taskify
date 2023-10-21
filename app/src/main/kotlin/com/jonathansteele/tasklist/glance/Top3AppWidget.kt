package com.jonathansteele.tasklist.glance

import android.content.Context
import android.os.Build
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.R

class Top3AppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val databaseHelper = DatabaseHelper(AndroidSqliteDriver(Database.Schema, context, "notes.db"))
        val tasks = databaseHelper.getTopTaskNames()
        provideContent {
            GlanceTheme(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    GlanceTheme.colors
                else
                    MyAppWidgetGlanceColorScheme.colors
            ) {
                LazyColumn(modifier = GlanceModifier.background(GlanceTheme.colors.surfaceVariant)) {
                    item {
                        Text(LocalContext.current.getString(R.string.app_name))
                    }
                    items(tasks) {
                        Text(
                            text = it,
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    /*@Composable
    fun MyContent() {

    }*/
}