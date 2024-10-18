import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.0-alpha07" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                    layout.buildDirectory.asFile
                        .get()
                        .absolutePath + "/compose_metrics",
            )
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                    layout.buildDirectory.asFile
                        .get()
                        .absolutePath + "/compose_metrics",
            )
        }
    }
}
