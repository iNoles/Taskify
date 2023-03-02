package com.jonathansteele.tasklist

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import com.jonathansteele.Task
import com.jonathansteele.TaskList
import com.jonathansteele.tasklist.ui.theme.TaskListTheme
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(navController: NavController<Screen>) {
    val context = LocalContext.current
    val database = DatabaseHelper(context)
    val pages = database.getAllPages()
    LoadPagerWithTitle(pages = pages, navController = navController, database = database)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadPagerWithTitle(
    pages: List<TaskList>,
    navController: NavController<Screen>,
    database: DatabaseHelper
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Task List") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.Add(pages, database))
            }) {
                Icon(Icons.Filled.Add, "")
            }
        },
    ) {
        GetListTitleFromDatabase(
            pages = pages,
            database = database,
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GetListTitleFromDatabase(
    modifier: Modifier = Modifier,
    database: DatabaseHelper,
    pages: List<TaskList>
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        modifier = modifier
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(title.name) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
            )
        }
    }

    HorizontalPager(
        pageCount = pages.size,
        state = pagerState,
    ) { page ->
        val content = remember {
            database.getAllTasksBySpecificPageId(page)
        }
        val pager = content.collectAsState(initial = emptyList()).value
        ListPagerContent(
            database = database,
            pager = pager,
            coroutineScope = coroutineScope)
    }
}

@Composable
fun ListPagerContent(pager: List<Task>, coroutineScope: CoroutineScope, database: DatabaseHelper) {
    LazyColumn {
        items(pager) { task ->
            ListItem(
                leadingContent = {
                    Checkbox(
                        checked = task.completedDate.toLong() > 0,
                        onCheckedChange = {
                            coroutineScope.launch {
                                val date = if (it) {
                                    System.currentTimeMillis().toString()
                                } else {
                                    "0"
                                }
                                database.insertTask(task, date)
                            }
                        })
                },
                headlineText = { Text(text = task.name) },
                supportingText = { Text(text = task.notes) }
            )
        }
    }
}

@Preview(showBackground = true, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Composable
fun TaskScreenLightPreview() {
    TaskListTheme {
        LoadPagerWithTitle(
            pages= listOf(
                TaskList(0, "Personal"),
                TaskList(1, "Business")
            ),
            navController = rememberNavController(startDestination = Screen.Task),
            database = DatabaseHelper(context = LocalContext.current)
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun TaskScreenDarkPreview() {
    TaskListTheme {
        LoadPagerWithTitle(
            pages= listOf(
                TaskList(0, "Personal"),
                TaskList(1, "Business")
            ),
            navController = rememberNavController(startDestination = Screen.Task),
            database = DatabaseHelper(context = LocalContext.current)
        )
    }
}

@Preview(showBackground = true, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Composable
fun ListPagerLightPreview() {
    TaskListTheme {
        val coroutineScope = rememberCoroutineScope()
        val pager = listOf(
            Task(0, 0, "Improving Resume", "Making my resume more stand out", "0" ,0),
            Task(1, 0, "Prepare Interview", "Go over of interview questions", "0" ,0)
        )
        ListPagerContent(
            pager = pager,
            coroutineScope = coroutineScope,
            database = DatabaseHelper(context = LocalContext.current)
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun ListPagerDarkPreview() {
    TaskListTheme {
        val coroutineScope = rememberCoroutineScope()
        val pager = listOf(
            Task(0, 0, "Improving Resume", "Making my resume more stand out", "0" ,0),
            Task(1, 0, "Prepare Interview", "Go over of interview questions", "0" ,0)
        )
        ListPagerContent(
            pager = pager,
            coroutineScope = coroutineScope,
            database = DatabaseHelper(context = LocalContext.current)
        )
    }
}
