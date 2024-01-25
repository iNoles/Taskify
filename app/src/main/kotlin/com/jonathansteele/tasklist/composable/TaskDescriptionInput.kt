package com.jonathansteele.tasklist.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TaskDescriptionInput(notes: MutableState<String>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
    ) {
        Text(
            text = "Enter Task notes",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
        ) {
            BasicTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                onValueChange = {
                    notes.value = it
                },
                value = notes.value,
            )
        }
    }
}
