package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.safeCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(
    private val client: SupabaseClient,
) : IAuthRepository {
    override suspend fun signUp(
        email: String,
        password: String,
    ) = safeCall {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Unit
    }

    override suspend fun signIn(
        email: String,
        password: String,
    ) = safeCall {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut() =
        safeCall {
            client.auth.signOut()
        }
}
