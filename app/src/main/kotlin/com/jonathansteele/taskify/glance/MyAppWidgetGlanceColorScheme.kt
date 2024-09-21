package com.jonathansteele.taskify.glance

import androidx.glance.material3.ColorProviders
import com.jonathansteele.taskify.theme.DarkThemeColors
import com.jonathansteele.taskify.theme.LightThemeColors

object MyAppWidgetGlanceColorScheme {
    val colors =
        ColorProviders(
            light = LightThemeColors,
            dark = DarkThemeColors,
        )
}
