package com.jonathansteele.tasklist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jonathansteele.tasklist.di.appModules
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@RunWith(AndroidJUnit4::class)
@Category(CheckModuleTest::class)
class KoinTest  {
    @Test
    fun checkAllModules() {
        startKoin {
            appModules
        }.checkModules()
    }
}