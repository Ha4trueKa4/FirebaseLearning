package com.example.firebaseapplication

data class ChatMessage(
    val text: String? = null,
    val sender: String? = "Anonymous",
    val timestamp: Long? = System.currentTimeMillis()
)