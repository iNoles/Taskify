package com.jonathansteele.taskify.data.model

enum class Priority(
    val value: Int,
) {
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    ;

    companion object {
        fun fromValue(value: Int): Priority = entries.find { it.value == value } ?: LOW
    }
}
