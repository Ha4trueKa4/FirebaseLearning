package com.example.firebaseapplication.auth

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import androidx.credentials.GetCredentialRequest
import com.example.firebaseapplication.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.tasks.await

suspend fun signInWithGoogle(context: Context): Result<Unit> {
    return try {
        val credentialManager = CredentialManager.create(context)
        val request = buildCredentialRequest(context)

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val googleCredential = GoogleIdTokenCredential
            .createFrom(result.credential.data)

        val firebaseCredential = GoogleAuthProvider.getCredential(
            googleCredential.idToken,
            null
        )

        FirebaseAuth.getInstance()
            .signInWithCredential(firebaseCredential)
            .await()

        Result.success(Unit)
    } catch (e: GetCredentialException) {
        Result.failure(e)
    }
}

fun buildCredentialRequest(context: Context): GetCredentialRequest {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(
            context.getString(R.string.default_web_client_id)
        )
        .build()

    return GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
}

fun logout(context: Context) {
    FirebaseAuth.getInstance().signOut()
    val intent = Intent(context, AuthActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}