package com.jonathansteele.taskify

import com.jonathansteele.taskify.data.repository.AuthRepository
import com.jonathansteele.taskify.data.repository.TaskRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules =
    module {
        single {
            createSupabaseClient(
                supabaseUrl = "YOUR_SUPABASE_URL",
                supabaseKey = "YOUR_SUPABASE_KEY",
            ) {
                defaultSerializer =
                    KotlinXSerializer(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            encodeDefaults = true
                        },
                    )
                install(Auth)
                install(Postgrest)
                install(Realtime)
            }
        }

        single { AuthRepository(get()) }
        single { TaskRepository(get(), get()) }
        viewModelOf(::HomeViewModel)
    }
