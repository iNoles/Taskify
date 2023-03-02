package com.jonathansteele.tasklist

import android.os.Parcelable
import com.jonathansteele.TaskList
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class Screen : Parcelable {

    @Parcelize
    object Task : Screen()

    @Parcelize
    data class Add(
        val page: @RawValue List<TaskList>,
        val database: @RawValue DatabaseHelper
    ) : Screen()
}