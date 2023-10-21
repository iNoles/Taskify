package com.jonathansteele.tasklist.glance

import androidx.glance.material3.ColorProviders
import com.jonathansteele.tasklist.theme.DarkThemeColors
import com.jonathansteele.tasklist.theme.LightThemeColors

object MyAppWidgetGlanceColorScheme {
    val colors = ColorProviders(
        light = LightThemeColors,
        dark = DarkThemeColors
    )
}