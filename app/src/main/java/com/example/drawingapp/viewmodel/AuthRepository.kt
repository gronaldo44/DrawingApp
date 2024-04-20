package com.example.drawingapp.viewmodel

import android.content.Context
import android.util.Log
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class AuthRepository {
    val db = Firebase.firestore
    val collection = db.collection("drawingAppCollection")

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

    fun uploadToDb(drawing: Drawing) : Boolean{
        var success = true
        val serializedPathData = DrawingSerializer.fromPathDataList(drawing.paths)
        collection.add(serializedPathData).addOnSuccessListener {
            Log.d("Upload to Firebase", "Drawing uploaded successfully")
        }.addOnFailureListener{e ->
            Log.e("Upload to Firebase", "Error uploading drawing: ${e.message}")
            success = false
        }
        return success
    }

    fun loadAllDrawingsFromDb(): ArrayList<Drawing> {
        val convertedDrawings = ArrayList<Drawing>()
        collection.get().addOnSuccessListener { result ->
            Log.d("Read from Firebase", "Successfully got " + result.documents.count() + " documents.")
            val docs = result.documents
            for (doc in docs) {
                // Convert each document to a DbDrawing
                val data = doc.getString("drawing")
                val name = doc.getString("name")
                val author = doc.getString("author")
                val id = doc.getLong("id")

                if (data != null && name != null && author != null && id != null) {
                    val drawing = DrawingSerializer.toDrawing(data, name, author)
                    Log.d("Converted FB Drawing", id.toString())
                    convertedDrawings.add(drawing)
                }
            }

        }.addOnFailureListener{ e ->
            Log.e("Load from Firebase", "Error downloading drawing: ${e.message}")
        }
        return convertedDrawings
    }
}
