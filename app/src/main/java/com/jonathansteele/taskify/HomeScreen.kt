package com.jonathansteele.taskify

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jonathansteele.taskify.database.Task
import com.jonathansteele.taskify.database.TaskList
import com.jonathansteele.taskify.database.TaskListDao
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    taskListDao: TaskListDao = koinInject(),
    taskRepository: TaskRepository = koinInject()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Task List") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { navController.navigate("add") }) {
                Text("Create")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            GetListTitleFromDatabase(taskListDao, taskRepository)
        }
    }
}

@Composable
fun GetListTitleFromDatabase(
    taskListDao: TaskListDao,
    taskRepository: TaskRepository
) {
    val pages = remember { mutableStateListOf<TaskList>() }
    val pagerState = rememberPagerState { pages.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        pages.clear()
        pages.addAll(taskListDao.getAll())
    }

    val tasks = remember { mutableStateListOf<Task>() }
    LaunchedEffect(pagerState.currentPage) {
        tasks.clear()
        tasks.addAll(taskRepository.getAllTasksByListId(pagerState.currentPage))
    }

    TabRow(selectedTabIndex = pagerState.currentPage) {
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(title.name) },
                selected = pagerState.currentPage == index,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
            )
        }
    }

    HorizontalPager(state = pagerState) { page ->
        ListPagerContent(
            pager = tasks,
            onCheckedChange = { isChecked, task ->
                coroutineScope.launch {
                    task.completedDate = (if (isChecked) System.currentTimeMillis() else "0").toString()
                    taskRepository.insertTask(task)
                }
            },
            deleteChange = { taskId ->
                coroutineScope.launch {
                    taskRepository.deleteTaskById(taskId)
                }
            }
        )
    }
}

@Composable
fun ListPagerContent(
    pager: List<Task>,
    onCheckedChange: (Boolean, Task) -> Unit,
    deleteChange: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(pager) { task ->
            Card {
                ListItem(
                    leadingContent = {
                        Checkbox(
                            checked = task.completedDate != "0",
                            onCheckedChange = { onCheckedChange(it, task) }
                        )
                    },
                    headlineContent = { Text(text = task.name) },
                    supportingContent = { Text(text = task.notes) },
                    trailingContent = {
                        IconButton(onClick = { deleteChange(task.uid) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
    }
}
