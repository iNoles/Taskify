package com.jonathansteele.tasklist.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.Task
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.navigation.Screen
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController<Screen>,
    databaseHelper: DatabaseHelper,
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Task List") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Add)
                },
            ) {
                Text("Create")
            }
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            GetListTitleFromDatabase(
                database = databaseHelper,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GetListTitleFromDatabase(database: DatabaseHelper) {
    val pagerState =
        rememberPagerState {
            database.getAllPages().size
        }
    val coroutineScope = rememberCoroutineScope()
    val pages = database.getAllPages()
    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
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

    HorizontalPager(state = pagerState) { page ->
        val content =
            remember {
                database.getAllTasksBySpecificPageId(page)
            }
        val pager = content.collectAsState(initial = emptyList()).value
        ListPagerContent(
            pager = pager.toImmutableList(),
            onCheckedChange = { boolean, task ->
                coroutineScope.launch {
                    val date =
                        if (boolean) {
                            System.currentTimeMillis().toString()
                        } else {
                            "0"
                        }
                    database.insertTask(task, date)
                }
            },
            deleteChange = {
                coroutineScope.launch {
                    database.deleteTask(it)
                }
            },
        )
    }
}

@Composable
fun ListPagerContent(
    pager: ImmutableList<Task>,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean, Task) -> Unit,
    deleteChange: (Long) -> Unit,
) {
    LazyColumn {
        items(pager) { task ->
            Card(modifier.padding(16.dp)) {
                ListItem(
                    leadingContent = {
                        Checkbox(
                            checked = task.completedDate.toLong() > 0,
                            onCheckedChange = {
                                onCheckedChange(it, task)
                            },
                        )
                    },
                    headlineContent = { Text(text = task.name) },
                    supportingContent = { Text(text = task.notes) },
                    trailingContent = {
                        IconButton(onClick = { deleteChange(task.id) }) {
                            Icon(Icons.Filled.Delete, "Delete")
                        }
                    },
                )
            }
        }
    }
}
