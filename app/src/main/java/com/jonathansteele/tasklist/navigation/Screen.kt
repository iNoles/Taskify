package com.jonathansteele.tasklist.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screen : Parcelable {

    @Parcelize
    object Task : Screen()

    @Parcelize
    object Add : Screen()
}