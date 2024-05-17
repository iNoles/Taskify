import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0-alpha02" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0-RC3" apply false
    id("app.cash.sqldelight") version "2.0.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0-RC3" apply false
}

allprojects {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                    layout.buildDirectory.asFile.get().absolutePath + "/compose_metrics",
            )
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                    layout.buildDirectory.asFile.get().absolutePath + "/compose_metrics",
            )
        }
    }
}
