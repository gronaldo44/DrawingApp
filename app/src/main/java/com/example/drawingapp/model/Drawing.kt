package com.example.drawingapp.model

import android.graphics.Path
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Regular Drawing class used in the rest of the project
 */
data class Drawing(val paths: ArrayList<PathData>) {
    var id: Long = 0
    data class PathData(val path: Path, val color: Int, val size: Float)
}

// DbDrawing class for database operations (serialization of paths)
@Entity(tableName = "Drawing")
data class DbDrawing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pathDataString: String // Serialized paths (JSON representation)
)

// Converter for Path to String and vice versa
object PathDataConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromPathDataList(pathDataList: ArrayList<Drawing.PathData>): String {
        return gson.toJson(pathDataList)
    }

    @TypeConverter
    fun toPathDataList(pathDataString: String): ArrayList<Drawing.PathData> {
        val type = object : TypeToken<ArrayList<Drawing.PathData>>() {}.type
        return gson.fromJson(pathDataString, type)
    }
}

// Converter for Drawing to DbDrawing and vice versa
object DrawingConverter {
    private val gson = Gson()
    @TypeConverter
    fun fromDrawing(drawing: Drawing): DbDrawing {
        return DbDrawing(drawing.id, Gson().toJson(drawing.paths))
    }

    @TypeConverter
    fun toDrawing(dbDrawing: DbDrawing): Drawing {
        val paths = Gson().fromJson(dbDrawing.pathDataString, object :
            TypeToken<ArrayList<Drawing.PathData>>() {}.type) as ArrayList<Drawing.PathData>
        return Drawing(paths)
    }
}