package com.example.firebaseapplication.chat


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.firebaseapplication.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize(), topBar = { MessageActionBar() }) { innerPadding ->
                MessageForm(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}


fun sendMessage(messageText: String, messagesRef : DatabaseReference) {
    val message = ChatMessage(
        messageText,
        FirebaseAuth.getInstance().currentUser?.displayName ?: "Guest",
        System.currentTimeMillis()
    )
    messagesRef.push().setValue(message)
}