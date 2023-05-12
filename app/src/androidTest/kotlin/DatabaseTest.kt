import android.app.Application
import androidx.test.core.app.ApplicationProvider
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.tasklist.DatabaseHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DatabaseTest {
    private lateinit var helper: DatabaseHelper

    @Before
    fun before() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val driver = AndroidSqliteDriver(Database.Schema, app, "notes.db")
        helper = DatabaseHelper(driver)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertTest() = runTest {
        var numItemsBeforeInsertion = -1
        helper.getAllTasksBySpecificPageId(0).collect {
            numItemsBeforeInsertion = it.size
        }


        helper.insertTask(name = "Test", notes = "This is test", listId = 0L, hidden = 0L)
        helper.insertTask(name = "Interview", notes = "Preparing for Interview", listId = 0L, hidden = 0L)

        var numItemsAfterInsertion = -1
        helper.getAllTasksBySpecificPageId(0).collect {
            numItemsAfterInsertion = it.size
        }

        assertEquals(0, numItemsBeforeInsertion)
        assertEquals(2, numItemsAfterInsertion)
    }
}