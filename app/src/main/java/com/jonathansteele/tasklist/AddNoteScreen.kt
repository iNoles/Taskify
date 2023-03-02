package com.jonathansteele.tasklist

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import com.jonathansteele.TaskList
import com.jonathansteele.tasklist.ui.theme.TaskListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(screen: Screen.Add, goBack: () -> Unit) {
    val pages = screen.page
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(pages[0]) }
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Add Task") },
                actions = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            val scope = CoroutineScope(Dispatchers.Main)
                            scope.launch {
                                screen.database.insertTask(
                                    id = null,
                                    listId = selectedOptionText.id,
                                    name = name,
                                    notes = notes
                                )
                            }
                            goBack()
                        }
                    )
                }
            ) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOptionText.name,
                    onValueChange = { },
                    label = { Text("Label") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier.menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    pages.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption.name) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") }
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                singleLine = true
            )
        }
    }
}

@Preview(showBackground = true, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Composable
fun AddNoteLightPreview() {
    TaskListTheme {
        AddNoteScreen(
            Screen.Add(
                page = listOf(
                    TaskList(0,"Personal"),
                    TaskList(1, "Business")
                ),
                database = DatabaseHelper(context = LocalContext.current)
            ), goBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun AddNoteDarkPreview() {
    TaskListTheme {
        AddNoteScreen(
            Screen.Add(
                page = listOf(
                    TaskList(0,"Personal"),
                    TaskList(1, "Business")
                ),
                database = DatabaseHelper(context = LocalContext.current)
            ), goBack = {}
        )
    }
}
