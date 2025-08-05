package com.jonathansteele.taskify.data.repository

object FakeAuthRepository : IAuthRepository {
    override suspend fun signIn(
        email: String,
        password: String,
    ) = Result.success(Unit)

    override suspend fun signUp(
        email: String,
        password: String,
    ) = Result.success(Unit)

    override suspend fun signOut() = Result.success(Unit)
}
