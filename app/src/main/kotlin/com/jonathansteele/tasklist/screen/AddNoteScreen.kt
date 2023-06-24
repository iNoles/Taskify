package com.jonathansteele.tasklist.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.Task
import com.jonathansteele.TaskList
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.composable.TaskDescriptionInput
import com.jonathansteele.tasklist.composable.TaskDropDown
import com.jonathansteele.tasklist.composable.TaskNameInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(database: DatabaseHelper, task: Task? = null, goBack: () -> Unit) {
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
    goBack: () -> Unit
) {
    val pages = database.getAllPages()
    val notes = remember { mutableStateOf("") }
    val names = remember { mutableStateOf("") }
    val hiddenState = remember { mutableStateOf(false) }
    val selectedOptionText = remember { mutableStateOf(pages[0]) }
    task?.let {
        notes.value = it.notes
        names.value = it.name
        hiddenState.value = it.hidden == 1L
        selectedOptionText.value = pages[it.listId.toInt()]
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(paddingValues)
    ) {
        TaskNameInput(names = names)
        TaskDropDown(pages = pages, selectedOptionText = selectedOptionText)
        TaskDescriptionInput(notes = notes)
        TaskHiddenCheckBox(hiddenState = hiddenState)
        SaveButton(database, goBack, notes, names, hiddenState, selectedOptionText, task?.id)
    }
}

@Composable
fun TaskHiddenCheckBox(hiddenState: MutableState<Boolean>) {
    // in below line we are displaying a row
    // and we are creating a checkbox in a row.
    Row {
        Checkbox(
            // below line we are setting
            // the state of checkbox.
            checked = hiddenState.value,
            // below line is use to add padding
            // to our checkbox.
            modifier = Modifier.padding(16.dp),
            // below line is use to add on check
            // change to our checkbox.
            onCheckedChange = { hiddenState.value = it },
        )
        // below line is use to add text to our check box and we are
        // adding padding to our text of checkbox
        Text(text = "Task Hidden Enabled", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun SaveButton(
    database: DatabaseHelper,
    goBack: () -> Unit,
    notes: MutableState<String>,
    names: MutableState<String>,
    hiddenState: MutableState<Boolean>,
    selectedOptionText: MutableState<TaskList>,
    id: Long? = null,
) {
    FilledTonalButton(
        onClick = {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                database.insertTask(
                    id = id,
                    name = names.value,
                    notes = notes.value,
                    listId = selectedOptionText.value.id,
                    hidden = if (hiddenState.value) 1L else 0L
                )
                goBack()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = "Save Note",
            modifier = Modifier.padding(6.dp),
        )
    }
}
