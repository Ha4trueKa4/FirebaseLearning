package com.example.firebaseapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.firebaseapplication.auth.logout

@Composable
fun LogoutButton() {
    val context = LocalContext.current

    IconButton(
        onClick = { logout(context) }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = "Logout"
        )
    }
}