package com.example.firebaseapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


data class ChatMessage(
    val text: String? = null,
    val sender: String? = "Anonymous",
    val timestamp: Long? = System.currentTimeMillis()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MessageForm(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}


fun sendMessage(messageText: String) {
    val database = Firebase.database

    val messagesRef = database.getReference("messages")
    val message = ChatMessage(messageText, "Current User", System.currentTimeMillis())
    messagesRef.push().setValue(message)
}

@Composable
fun MessageForm(modifier: Modifier = Modifier) {
    val messageStory = remember { mutableStateListOf<ChatMessage>() }
    var text by rememberSaveable { mutableStateOf("") }

    val messagesRef = remember { Firebase.database.getReference("messages") }

    DisposableEffect(messagesRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageStory.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let {
                        messageStory.add(it)
                    }
                }
                messageStory.sortBy { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        }

        messagesRef.addValueEventListener(valueEventListener)

        onDispose {
            messagesRef.removeEventListener(valueEventListener)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f) // Занимает всю доступную высоту
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messageStory) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = message.text ?: "No message text")
                        Text(
                            text = "От: ${message.sender ?: "Unknown"}",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                        )
                        // Можно добавить форматирование времени, если нужно
                        Text(
                            text = "Время: ${
                                message.timestamp?.let {
                                    java.text.SimpleDateFormat(
                                        "HH:mm",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date(it))
                                } ?: "N/A"
                            }",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
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
                        sendMessage(text)
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

