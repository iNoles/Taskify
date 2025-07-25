package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.safeCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(
    private val client: SupabaseClient,
) {
    suspend fun signUp(
        email: String,
        password: String,
    ) = safeCall {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Unit
    }

    suspend fun signIn(
        email: String,
        password: String,
    ) = safeCall {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() =
        safeCall {
            client.auth.signOut()
        }

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id
}
