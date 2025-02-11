package com.jonathansteele.taskify.glance

import android.content.Context
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
import com.jonathansteele.taskify.R
import com.jonathansteele.taskify.database.TaskDao
import org.koin.core.context.GlobalContext

class Top3AppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        // Get Koin instance to access the database
        val koin = GlobalContext.get()
        val taskDao: TaskDao = koin.get()
        val tasks = taskDao.getTop3TasksName()
        provideContent {
            GlanceTheme(GlanceTheme.colors) {
                LazyColumn(modifier = GlanceModifier.background(GlanceTheme.colors.surfaceVariant)) {
                    item {
                        Text(LocalContext.current.getString(R.string.app_name))
                    }
                    items(tasks) {
                        Text(
                            text = it,
                            modifier = GlanceModifier.fillMaxWidth(),
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
