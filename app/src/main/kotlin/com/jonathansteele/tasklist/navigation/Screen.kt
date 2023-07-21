package com.jonathansteele.tasklist.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screen : Parcelable {

    @Parcelize
    data object Task : Screen()

    @Parcelize
    data object Add : Screen()
}