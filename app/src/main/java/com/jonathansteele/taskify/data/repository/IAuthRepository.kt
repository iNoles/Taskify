package com.jonathansteele.taskify.data.repository

interface IAuthRepository {
    suspend fun signIn(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun signOut(): Result<Unit>
}
