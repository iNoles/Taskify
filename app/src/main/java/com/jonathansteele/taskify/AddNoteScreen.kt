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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = if (taskId == -1) "Add Task" else "Edit Task") },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        EventInputs(
            paddingValues = paddingValues,
            taskId = taskId,
            taskRepository = taskRepository,
            taskListDao = listDao,
            snackbarHostState = snackbarHostState,
            scope = scope,
            goBack = goBack,
        )
    }
}

@Composable
fun EventInputs(
    paddingValues: PaddingValues,
    taskId: Int,
    taskRepository: TaskRepository,
    taskListDao: TaskListDao,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    goBack: () -> Unit,
) {
    val context = LocalContext.current
    val pages = remember { mutableStateOf<List<TaskList>>(emptyList()) }
    val selectedOptionText = remember { mutableStateOf<TaskList?>(null) }

    LaunchedEffect(Unit) {
        val fetchedPages = taskListDao.getAll()
        pages.value = fetchedPages
        selectedOptionText.value = fetchedPages.firstOrNull()
    }

    val names = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val hiddenState = remember { mutableStateOf(false) }
    val dueState = remember { mutableStateOf(getTodayDate()) }
    val priority = remember { mutableStateOf(Priority.LOW) }

    if (taskId != -1) {
        LaunchedEffect(taskId) {
            taskRepository.getTaskById(taskId).let {
                names.value = it.name
                notes.value = it.notes
                hiddenState.value = it.hidden == 1
                selectedOptionText.value =
                    pages.value.find { list -> list.uid == it.listId } ?: pages.value.firstOrNull()
                priority.value = Priority.valueOf(it.priority.toString())
                dueState.value = it.dueDate.takeIf { it?.isNotEmpty() == true } ?: getTodayDate()
            }
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
    ) {
        if (names.value.isBlank() || notes.value.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Task name and notes cannot be empty")
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
                )

            if (taskId == -1) {
                taskRepository.insertTask(task)
            } else {
                taskRepository.updateTask(task)
            }

            scheduleNotification(context, dueState.value, names.value)
            snackbarHostState.showSnackbar("Task saved successfully")
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
    buttonClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Enter Task Name", style = MaterialTheme.typography.bodyLarge)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = names.value,
            onValueChange = { names.value = it },
            placeholder = { Text("e.g. Task Name") },
            singleLine = true,
            isError = names.value.isEmpty(),
        )

        TaskDropDown(pages = pages, selectedOptionText = selectedOptionText)

        Text("Enter Task Notes", style = MaterialTheme.typography.bodyLarge)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = notes.value,
            onValueChange = { notes.value = it },
            placeholder = { Text("e.g. Notes") },
            isError = notes.value.isEmpty(),
        )

        Text("Priority Level", style = MaterialTheme.typography.bodyLarge)
        PriorityChips(priority)

        Text("Due Date", style = MaterialTheme.typography.bodyLarge)
        DatePicker(dueState)

        Row {
            Checkbox(
                checked = hiddenState.value,
                onCheckedChange = { hiddenState.value = it },
            )
            Text("Task Hidden", modifier = Modifier.padding(16.dp))
        }

        Button(
            onClick = buttonClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text("Save Task", style = MaterialTheme.typography.bodyLarge)
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
                            SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault(),
                            ).format(calendar.time)
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

    Button(onClick = { datePickerShown.value = true }) {
        Text("Pick Due Date")
    }
}

fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
