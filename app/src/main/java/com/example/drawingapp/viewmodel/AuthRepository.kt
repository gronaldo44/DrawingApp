package com.example.drawingapp.viewmodel

import android.content.Context
import android.util.Log
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
            Log.e("Logging in", "Log in failed ${e.stackTraceToString()}")
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

    // Upload serialized data to Cloud Storage
    fun uploadSerializedData(username: String, drawingId: String, serializedData: String) {
        // Ensure user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("Upload Drawing", "User is not authenticated.")
            return
        }
        // Ensure Firebase.auth.uid matches FirebaseAuth.getInstance().currentUser!!.uid
        val userId = currentUser.uid
        if (userId != Firebase.auth.uid) {
            Log.e("Upload Drawing", "Firebase.auth.uid does not match FirebaseAuth.getInstance().currentUser!!.uid")
            return
        }

        val storageReference = Firebase.storage.reference.child("drawings/$username/$drawingId.json")
        val serializedDataBytes = serializedData.toByteArray()

        storageReference.putBytes(serializedDataBytes)
            .addOnSuccessListener { _ ->
                Log.d("Uploading Drawing", "Successfully uploaded drawing to the cloud.")
                // Now store reference to Cloud Storage in Firestore
                val firestore = Firebase.firestore
                val drawingRef = firestore.collection("$username.drawings").document("/$drawingId")

                val data = hashMapOf(
                    "author_uid" to userId,
                    "path" to storageReference.path // Store the path to the file in Cloud Storage
                    // Other metadata like drawing name, author, etc. can be added here
                )

                drawingRef.set(data)
                    .addOnSuccessListener {
                        Log.d("Uploading Drawing Ref", "Reference stored successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Uploading Drawing Ref", "Failed to store reference ${e.stackTraceToString()}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Uploading Drawing", "Failed to upload drawing to the cloud. ${e.stackTraceToString()}")
            }
    }

    // Retrieve all drawings from Firestore
    fun retrieveDrawings(username: String): Task<ArrayList<Drawing>> {
        val firestore = Firebase.firestore
        val drawingsRef = firestore.collection("$username.drawings")

        // Define a Task to asynchronously retrieve drawings
        val task = TaskCompletionSource<ArrayList<Drawing>>()

        // Retrieve all documents from the user's drawings collection
        drawingsRef.get()
            .addOnSuccessListener { documents ->
                Log.d("Downloading Documents", "Successfully downloaded all of $username's documents.")
                val drawingsList = ArrayList<Drawing>()

                // Iterate over each document and convert it to a Drawing object
                for (document in documents) {
                    val drawingId = document.id
                    val path = document.getString("path")
                    if (path != null) {
                        Log.d("Downloading Document", "Path: $path")
                        val storageReference = Firebase.storage.getReference(path)
                        storageReference.getBytes(Long.MAX_VALUE)
                            .addOnSuccessListener { bytes ->
                                // Deserialize bytes to reconstruct drawing
                                val serializedData = String(bytes)
                                val drawing = DrawingSerializer.toDrawing(
                                    serializedData, path[path.length - 6].toString(), username)
                                drawingsList.add(drawing)
                                Log.d("Downloading Data", "Successfully downloaded ${drawing.name}.")

                                // Check if all drawings have been retrieved
                                if (drawingsList.size == documents.size()) {
                                    task.setResult(drawingsList)
                                }
                            }
                            .addOnFailureListener { e ->
                                // Handle failure to retrieve serialized data from Cloud Storage
                                Log.e("Downloading Data", "Failed to Download $username's serialized data. ${e.stackTraceToString()}")
                                task.setException(e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Downloading Documents", "Failed to Download $username's drawings. ${e.stackTraceToString()}")
                task.setException(e)
            }

        return task.task // Return the Task<ArrayList<Drawing>>
    }

}
