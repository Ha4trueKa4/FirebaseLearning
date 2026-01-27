package com.example.firebaseapplication.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.firebaseapplication.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FbChatViewModel : ViewModel() {
    private val messagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages")

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val list = mutableListOf<ChatMessage>()

            for (child in snapshot.children) {
                val message = child.getValue(ChatMessage::class.java)
                message?.let { list.add(it) }

            }
            _messages.value = list.sortedBy { it.timestamp }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Failed to read value", error.toException())
        }
    }

    init {
        messagesRef.addValueEventListener(listener)
    }

    fun sendMessage(messageText : String) {
        val message = ChatMessage(
            messageText,
            FirebaseAuth.getInstance().currentUser?.displayName ?: "Guest",
            System.currentTimeMillis()
        )
        messagesRef.push().setValue(message)
    }

    override fun onCleared() {
        messagesRef.removeEventListener(listener)
        super.onCleared()
    }
}