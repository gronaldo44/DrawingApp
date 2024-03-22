package com.example.drawingapp.model

import android.graphics.Path
import com.google.gson.Gson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Serializable
@Entity(tableName = "Drawing")
/**
 * Represents a drawing composed of paths with associated color and size information.
 */
class Drawing {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    val paths = ArrayList<PathData>()

    /**
     * Data class representing a single path with associated color and size.
     * @property path The path drawn by the user.
     * @property color The color of the path.
     * @property size The size of the path.
     */
    @Serializable
    data class PathData(@Contextual val path: Path, val color: Int, val size: Float) {

        @Contextual
        private val serializedPath: String = serializePath(path)

        /**
         * Custom getter to retrieve the Path object.
         */
        fun getDeserializedPath(): Path {
            val gson = Gson()
            return gson.fromJson(serializedPath, Path::class.java)
        }

        /**
         * Serialize the Path object to JSON.
         */
        private fun serializePath(path: Path): String {
            val gson = Gson()
            return gson.toJson(path)
        }
    }
}