package com.example.drawingapp.viewmodel

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    suspend fun login(email: String, password: String): Boolean {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Firebase.auth.currentUser != null
        } catch (e: Exception) {
            // Handle authentication failure (e.g., log error, return false)
            Log.e("Failed login", e.stackTraceToString())
            false
        }
    }

    suspend fun createUser(email: String, password: String): Boolean {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Firebase.auth.currentUser != null
        } catch (e: Exception) {
            // Handle authentication failure (e.g., log error, return false)
            Log.e("Failed to create user", e.stackTraceToString())
            false
        }
    }
}
