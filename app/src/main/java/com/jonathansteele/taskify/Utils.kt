package com.jonathansteele.taskify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

suspend fun <T> safeCall(action: suspend () -> T): Result<T> =
    withContext(Dispatchers.IO) {
        try {
            Result.success(action())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

fun <T : Enum<T>> EnumEntries<T>.names() = this.map { it.name }
