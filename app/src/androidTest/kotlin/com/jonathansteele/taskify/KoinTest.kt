package com.jonathansteele.taskify

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jonathansteele.taskify.di.appModules
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verifyAll

@RunWith(AndroidJUnit4::class)
class KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        appModules.verifyAll()
    }
}
