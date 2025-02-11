package com.jonathansteele.taskify.database

import androidx.room.TypeConverter
import com.jonathansteele.taskify.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): Int = priority.value

    @TypeConverter
    fun toPriority(value: Int): Priority =
        when (value) {
            1 -> Priority.HIGH
            2 -> Priority.MEDIUM
            else -> Priority.LOW
        }
}
