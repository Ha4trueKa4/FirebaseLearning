package com.example.firebaseapplication.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MessageForm(
    modifier: Modifier = Modifier,
    viewModel: FbChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = message.text ?: "No message text")
                        Text(
                            text = "От: ${message.sender ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Время: ${
                                message.timestamp?.let {
                                    java.text.SimpleDateFormat(
                                        "HH:mm",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date(it))
                                } ?: "N/A"
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Сообщение") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Отправить")
            }
        }
    }
}

