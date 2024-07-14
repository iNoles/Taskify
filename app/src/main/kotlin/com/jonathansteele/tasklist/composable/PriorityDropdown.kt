package com.jonathansteele.tasklist.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.jonathansteele.tasklist.Priority

@Composable
fun PriorityDropdown(priority: MutableState<Priority>) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = { /*TODO*/ },
        modifier = Modifier.fillMaxWidth()
    ) {
        DropdownMenuItem(
            onClick = { priority.value = Priority.HIGH },
            text = { Text("High") }
        )
        DropdownMenuItem(
            onClick = { priority.value = Priority.MEDIUM },
            text = { Text("Medium") }
        )
        DropdownMenuItem(
            onClick = { priority.value = Priority.LOW },
            text = { Text("Low") }
        )
    }
}