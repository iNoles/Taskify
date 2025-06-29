package com.jonathansteele.taskify

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.taskify.database.Task
import com.jonathansteele.taskify.database.TaskListDao
import com.jonathansteele.taskify.database.seedIfEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    taskListDao: TaskListDao = koinInject(),
    taskRepository: TaskRepository = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    // Seed data on launch
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            seedIfEmpty(taskListDao)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Task List") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Outlined.Add, contentDescription = "Add Task") },
                text = { Text("Add Task") },
            )
        },
    ) { paddingValues ->
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
        ) {
            GetListTitleFromDatabase(taskListDao, taskRepository, coroutineScope, onEditClick)
        }
    }
}

@Composable
fun GetListTitleFromDatabase(
    taskListDao: TaskListDao,
    taskRepository: TaskRepository,
    coroutineScope: CoroutineScope,
    onEditClick: (Int) -> Unit,
) {
    val pagesState =
        produceState(initialValue = emptyList()) {
            value = taskListDao.getAll()
        }

    val pages = pagesState.value

    if (pages.isNotEmpty()) {
        val pagerState =
            rememberPagerState(
                initialPage = 0,
                pageCount = { pages.size },
            )

        val currentList = pages.getOrNull(pagerState.currentPage)
        val tasksFlow = currentList?.let { taskRepository.getAllTasksByListIdFlow(it.uid) }
        val tasks by tasksFlow?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

        Column {
            SecondaryTabRow(
                pagerState.currentPage,
                tabs = {
                    pages.forEachIndexed { index, page ->
                        Tab(
                            text = { Text(page.name) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                },
            )

            HorizontalPager(
                state = pagerState,
            ) {
                ListPagerContent(
                    tasks = tasks,
                    onCheckedChange = { isChecked, task ->
                        coroutineScope.launch {
                            taskRepository.insertTask(task.withCompletion(isChecked))
                        }
                    },
                    deleteChange = { taskId ->
                        coroutineScope.launch {
                            taskRepository.deleteTaskById(taskId)
                        }
                    },
                    onEdit = { task ->
                        onEditClick(task.uid)
                    },
                )
            }
        }
    }
}

@Composable
fun ListPagerContent(
    tasks: List<Task>,
    onCheckedChange: (Boolean, Task) -> Unit,
    deleteChange: (Int) -> Unit,
    onEdit: (Task) -> Unit,
) {
    if (tasks.isEmpty()) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.MailOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "No tasks yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(6.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    ListItem(
                        leadingContent = {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { isChecked -> onCheckedChange(isChecked, task) },
                            )
                        },
                        headlineContent = { Text(task.name) },
                        supportingContent = {
                            if (task.notes.isNotBlank()) {
                                Text(task.notes)
                            }
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { onEdit(task) }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Task")
                                }
                                IconButton(onClick = { deleteChange(task.uid) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete Task")
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
