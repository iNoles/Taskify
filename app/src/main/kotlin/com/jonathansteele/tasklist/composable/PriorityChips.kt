package com.jonathansteele.tasklist.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.tasklist.Priority

@Composable
fun PriorityChips(priority: MutableState<Priority>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            onClick = { priority.value = Priority.LOW },
            label = {
                Text("Low")
            },
            selected = priority.value == Priority.LOW,
            leadingIcon = if (priority.value == Priority.LOW) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
            } else {
                null
            },
        )

        FilterChip(
            onClick = { priority.value = Priority.MEDIUM },
            label = {
                Text("Medium")
            },
            selected = priority.value == Priority.MEDIUM,
            leadingIcon = if (priority.value == Priority.MEDIUM) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
        )

        FilterChip(
            onClick = { priority.value = Priority.HIGH },
            label = {
                Text("Large")
            },
            selected = priority.value == Priority.HIGH,
            leadingIcon = if (priority.value == Priority.HIGH) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
            } else {
                null
            },
        )
    }
}
