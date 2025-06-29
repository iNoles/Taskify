package com.jonathansteele.taskify.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.taskify.Priority

@Composable
fun PriorityChips(
    priority: MutableState<Priority>,
    modifier: Modifier = Modifier,
) {
    val doneIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = "Done icon",
            modifier = Modifier.size(FilterChipDefaults.IconSize),
        )
    }

    val priorities =
        listOf(
            Priority.LOW to "Low",
            Priority.MEDIUM to "Medium",
            Priority.HIGH to "High",
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        priorities.forEach { (prio, label) ->
            val selected = priority.value == prio

            FilterChip(
                onClick = { priority.value = prio },
                label = { Text(label) },
                selected = selected,
                leadingIcon = if (selected) doneIcon else null,
                colors =
                    FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}
