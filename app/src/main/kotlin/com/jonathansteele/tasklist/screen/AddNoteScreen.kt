package com.jonathansteele.tasklist.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonathansteele.Task
import com.jonathansteele.TaskList
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.Priority
import com.jonathansteele.tasklist.composable.PriorityChips
import com.jonathansteele.tasklist.composable.TaskDropDown
import com.jonathansteele.tasklist.theme.TaskListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    database: DatabaseHelper,
    task: Task? = null,
    goBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Add Task") },
            )
        },
    ) { paddingValues ->
        EventInputs(paddingValues = paddingValues, database, task, goBack)
    }
}

@Composable
fun EventInputs(
    paddingValues: PaddingValues,
    database: DatabaseHelper,
    task: Task? = null,
    goBack: () -> Unit,
) {
    val pages = database.getAllPages()
    val notes = remember { mutableStateOf("") }
    val names = remember { mutableStateOf("") }
    val hiddenState = remember { mutableStateOf(false) }
    val selectedOptionText = remember { mutableStateOf(pages[0]) }
    val priority = remember { mutableStateOf(Priority.LOW) }

    task?.let {
        notes.value = it.notes
        names.value = it.name
        hiddenState.value = it.hidden == 1L
        selectedOptionText.value = pages[it.listId.toInt()]
        priority.value = Priority.valueOf(it.priority)
    }

    FormDisplay(
        paddingValues,
        names = names,
        pages = pages,
        selectedOptionText = selectedOptionText,
        notes = notes,
        priority = priority,
        hiddenState = hiddenState,
    ) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            database.insertTask(
                id = task?.id,
                name = names.value,
                notes = notes.value,
                listId = selectedOptionText.value.id,
                priority = priority.value,
                hidden = if (hiddenState.value) 1L else 0L,
            )
            goBack()
        }
    }
}

@Composable
fun FormDisplay(
    paddingValues: PaddingValues,
    names: MutableState<String>,
    pages: List<TaskList>,
    selectedOptionText: MutableState<TaskList>,
    notes: MutableState<String>,
    priority: MutableState<Priority>,
    hiddenState: MutableState<Boolean>,
    buttonClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Enter Task Name",
            style = MaterialTheme.typography.bodyLarge,
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = names.value,
            onValueChange = { names.value = it },
            placeholder = { Text(text = "e.g. any name you want") },
            singleLine = true,
            isError = names.value.isEmpty(),
        )

        Spacer(modifier = Modifier.padding(4.dp))

        TaskDropDown(pages = pages, selectedOptionText = selectedOptionText)

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "Enter Task Notes",
            style = MaterialTheme.typography.bodyLarge,
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = notes.value,
            onValueChange = { notes.value = it },
            placeholder = { Text(text = "e.g. any notes you want") },
            isError = notes.value.isEmpty(),
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "Enter Priority Level",
            style = MaterialTheme.typography.bodyLarge,
        )

        PriorityChips(priority)

        Spacer(modifier = Modifier.padding(4.dp))

        Row {
            Checkbox(
                checked = hiddenState.value,
                onCheckedChange = { hiddenState.value = it },
            )
            Text(text = "Task Hidden Enabled", modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            onClick = {
                buttonClick()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text = "Save Note",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddNoteScreenPreview() {
    TaskListTheme {
        val names = remember { mutableStateOf("") }
        val pages =
            listOf(
                TaskList(0, "Personal"),
                TaskList(1, "Business"),
            )
        val selectedOptionText = remember { mutableStateOf(pages[0]) }
        val notes = remember { mutableStateOf("") }
        val priority = remember { mutableStateOf(Priority.LOW) }
        val hiddenState = remember { mutableStateOf(false) }
        FormDisplay(
            PaddingValues(16.dp, 16.dp),
            names,
            pages,
            selectedOptionText,
            notes,
            priority,
            hiddenState,
        ) {}
    }
}
