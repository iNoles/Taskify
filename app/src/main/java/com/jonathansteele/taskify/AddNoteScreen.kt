package com.jonathansteele.taskify

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jonathansteele.taskify.composable.PriorityChips
import com.jonathansteele.taskify.composable.TaskDropDown
import com.jonathansteele.taskify.database.Task
import com.jonathansteele.taskify.database.TaskList
import com.jonathansteele.taskify.database.TaskListDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    taskId: Int = -1,
    taskRepository: TaskRepository = koinInject(),
    listDao: TaskListDao = koinInject(),
    goBack: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (taskId == -1) "Add Task" else "Edit Task",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Surface(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large,
            shadowElevation = 8.dp,
        ) {
            EventInputs(
                paddingValues = PaddingValues(24.dp),
                taskId = taskId,
                taskRepository = taskRepository,
                taskListDao = listDao,
                snackBarHostState = snackBarHostState,
                scope = scope,
                goBack = goBack,
            )
        }
    }
}

@Composable
fun EventInputs(
    paddingValues: PaddingValues,
    taskId: Int,
    taskRepository: TaskRepository,
    taskListDao: TaskListDao,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope,
    goBack: () -> Unit,
) {
    val context = LocalContext.current
    val pages = remember { mutableStateOf<List<TaskList>>(emptyList()) }
    val selectedOptionText = remember { mutableStateOf<TaskList?>(null) }

    val names = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val hiddenState = remember { mutableStateOf(false) }
    val dueState = remember { mutableStateOf(getTodayDate()) }
    val priority = remember { mutableStateOf(Priority.LOW) }
    val isCompleted = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val fetchedPages = taskListDao.getAll()
        pages.value = fetchedPages
        selectedOptionText.value = fetchedPages.firstOrNull()
    }

    if (taskId != -1) {
        LaunchedEffect(taskId) {
            val task = taskRepository.getTaskById(taskId)
            names.value = task.name
            notes.value = task.notes
            hiddenState.value = task.hidden == 1
            isCompleted.value = task.completedDate != Task.NOT_COMPLETED
            selectedOptionText.value =
                pages.value.find { list -> list.uid == task.listId } ?: pages.value.firstOrNull()
            priority.value = Priority.valueOf(task.priority.toString())
            dueState.value = task.dueDate.takeIf { it?.isNotEmpty() == true } ?: getTodayDate()
        }
    }

    FormDisplay(
        paddingValues,
        names,
        pages.value,
        selectedOptionText,
        notes,
        priority,
        dueState,
        hiddenState,
        isCompleted,
    ) {
        if (names.value.isBlank() || notes.value.isBlank()) {
            scope.launch {
                snackBarHostState.showSnackbar("Task name and notes cannot be empty")
            }
            return@FormDisplay
        }

        scope.launch {
            val task =
                Task(
                    uid = if (taskId == -1) 0 else taskId,
                    name = names.value,
                    notes = notes.value,
                    listId = selectedOptionText.value?.uid ?: 0,
                    priority = priority.value.value,
                    dueDate = dueState.value,
                    hidden = if (hiddenState.value) 1 else 0,
                    completedDate =
                        if (isCompleted.value) {
                            System.currentTimeMillis().toString()
                        } else {
                            Task.NOT_COMPLETED
                        },
                )

            if (taskId == -1) {
                taskRepository.insertTask(task)
            } else {
                taskRepository.updateTask(task)
            }

            scheduleNotification(context, task.uid, dueState.value, task.name)
            snackBarHostState.showSnackbar("Task saved successfully")
            goBack()
        }
    }
}

@Composable
fun FormDisplay(
    paddingValues: PaddingValues,
    names: MutableState<String>,
    pages: List<TaskList>,
    selectedOptionText: MutableState<TaskList?>,
    notes: MutableState<String>,
    priority: MutableState<Priority>,
    dueState: MutableState<String>,
    hiddenState: MutableState<Boolean>,
    isCompleted: MutableState<Boolean>,
    buttonClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("Enter Task Name", style = MaterialTheme.typography.titleMedium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = names.value,
            onValueChange = { names.value = it },
            placeholder = { Text("e.g. Task Name") },
            singleLine = true,
            isError = names.value.isEmpty(),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                ),
            shape = MaterialTheme.shapes.medium,
        )

        TaskDropDown(taskLists = pages, selectedTaskList = selectedOptionText)

        Text("Enter Task Notes", style = MaterialTheme.typography.titleMedium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = notes.value,
            onValueChange = { notes.value = it },
            placeholder = { Text("e.g. Notes") },
            isError = notes.value.isEmpty(),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                ),
            shape = MaterialTheme.shapes.medium,
        )

        Text("Priority Level", style = MaterialTheme.typography.titleMedium)
        PriorityChips(priority)

        Text("Due Date", style = MaterialTheme.typography.titleMedium)
        DatePicker(dueState)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Checkbox(
                checked = hiddenState.value,
                onCheckedChange = { hiddenState.value = it },
            )
            Text("Task Hidden", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Checkbox(
                checked = isCompleted.value,
                onCheckedChange = { isCompleted.value = it },
            )
            Text("Mark as Completed", modifier = Modifier.padding(start = 8.dp))
        }

        Button(
            onClick = buttonClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.filledTonalButtonColors(),
        ) {
            Text("Save Task", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(selectedDate: MutableState<String>) {
    val datePickerState = rememberDatePickerState()
    val datePickerShown = remember { mutableStateOf(false) }

    if (datePickerShown.value) {
        DatePickerDialog(
            onDismissRequest = { datePickerShown.value = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                        selectedDate.value =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(calendar.time)
                    }
                    datePickerShown.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { datePickerShown.value = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Button(
        onClick = { datePickerShown.value = true },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Text("Due: ${selectedDate.value}", style = MaterialTheme.typography.bodyLarge)
    }
}

fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
