package com.jonathansteele.taskify.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonathansteele.taskify.HomeViewModel
import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskListName
import com.jonathansteele.taskify.data.repository.FakeAuthRepository
import com.jonathansteele.taskify.data.repository.FakeTaskRepository
import com.jonathansteele.taskify.ui.theme.TaskifyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit = {},
    onEditClick: (Long) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task List") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout(onComplete = onLogout)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Log out",
                        )
                    }
                },
            )
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
            LoadTabRow(coroutineScope, viewModel, onEditClick)
        }
    }
}

@Composable
fun LoadTabRow(
    coroutineScope: CoroutineScope,
    viewModel: HomeViewModel,
    onEditClick: (Long) -> Unit,
) {
    val pages =
        listOf(
            TaskListName.Personal,
            TaskListName.Work,
        )
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { pages.size },
        )

    val tasksByList by viewModel.tasksByList.collectAsState()

    // Load tasks for the first tab initially
    LaunchedEffect(pagerState.currentPage) {
        viewModel.loadTasksFor(pages[pagerState.currentPage])
    }

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

        HorizontalPager(state = pagerState) { page ->
            val tasks = tasksByList[pages[page]] ?: emptyList()
            if (tasks.isEmpty()) {
                EmptyTasksBox()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(tasks, key = { it.id }) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onCheckedChange = { isChecked, task ->
                                viewModel.onTaskChecked(
                                    task,
                                    isChecked,
                                )
                            },
                            onEdit = onEditClick,
                            onDelete = { viewModel.deleteTask(task.id) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskItem(
    task: Task,
    onCheckedChange: (Boolean, Task) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Task) -> Unit,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
        )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onDelete(task)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
    ) {
        ElevatedCard(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
            elevation = CardDefaults.elevatedCardElevation(4.dp),
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
                    IconButton(onClick = { onEdit(task.id) }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Task")
                    }
                },
            )
        }
    }
}

@Composable
fun EmptyTasksBox() {
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskifyTheme {
        val viewModel =
            remember {
                HomeViewModel(FakeTaskRepository(), FakeAuthRepository).apply {
                    loadTasksFor(TaskListName.Personal)
                    loadTasksFor(TaskListName.Work)
                    loadTasksFor(TaskListName.Shared)
                }
            }
        HomeScreen(viewModel = viewModel)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenDarkPreview() {
    TaskifyTheme {
        val viewModel =
            remember {
                HomeViewModel(FakeTaskRepository(), FakeAuthRepository).apply {
                    loadTasksFor(TaskListName.Personal)
                    loadTasksFor(TaskListName.Work)
                    loadTasksFor(TaskListName.Shared)
                }
            }
        HomeScreen(viewModel = viewModel)
    }
}
