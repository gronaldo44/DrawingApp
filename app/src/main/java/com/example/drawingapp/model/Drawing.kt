package com.example.drawingapp.model

import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.Serializable
import java.io.File

/**
 * Represents a regular drawing containing paths.
 * @property paths The list of paths in the drawing.
 * @property id The identifier for the drawing.
 */
data class Drawing(var paths: ArrayList<PathData>, var name: String, var author: String) {
    var id: Long = -1   // tmp value
}

/**
 * Represents path data containing the path, color, and size information.
 * @property path The Path object representing the drawing path.
 * @property color The color of the path.
 * @property size The size of the path.
 */
data class PathData(val path: Path, var color: Int, val size: Float)
/**
 * Represents serialized path data for storage and retrieval.
 * @property path The serialized path data.
 * @property color The color of the path.
 * @property size The size of the path.
 */
data class SerializedPathData(val path: String, val color: String, val size: String)

/**
 * Represents a drawing entity for database operations.
 * @property fileDir The directory path where the drawing file is stored.
 */
@Serializable
@Entity(tableName = "Drawing")
data class DbDrawing(val fileDir: String, val name: String, val author: String){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

/**
 * Utility object for serializing and deserializing drawings.
 */
object DrawingSerializer {
    private val gson = Gson()

    /**
     * Converts a Path object to a serialized string representation.
     * @param path The Path object to convert.
     * @return The serialized string representation of the path.
     */
    fun pathToString(path: Path): String {
        val points = mutableListOf<Pair<Float, Float>>()
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length
        val step = 0.1f // Adjust step size as needed
        val pos = FloatArray(2)
        var distance = 0f
        while (distance < pathLength) {
            pathMeasure.getPosTan(distance, pos, null)
            points.add(Pair(pos[0], pos[1]))
            distance += step
        }
        return Gson().toJson(points)
    }

    /**
     * Converts a serialized string representation of a path to a Path object.
     * @param pathString The serialized string representation of the path.
     * @return The Path object reconstructed from the serialized string.
     */
    fun stringToPath(pathString: String): Path {
        val path = Path()
        val pointsType = object : TypeToken<List<Pair<Float, Float>>>() {}.type
        val points: List<Pair<Float, Float>> = Gson().fromJson(pathString, pointsType)
        points.forEachIndexed { index, point ->
            if (index == 0) {
                path.moveTo(point.first, point.second)
            } else {
                path.lineTo(point.first, point.second)
            }
        }
        return path
    }

    /**
     * Converts a list of PathData objects to a serialized string for storage.
     * @param pathDataList The list of PathData objects to convert.
     * @return The serialized string representation of the list of paths.
     */
    @TypeConverter
    fun fromPathDataList(pathDataList: ArrayList<PathData>): String {
        val serializedPaths: ArrayList<SerializedPathData> = ArrayList()
        pathDataList.forEach{ pathData ->
            val p = pathToString(pathData.path)
            val c = pathData.color.toString()
            val s = pathData.size.toString()
            serializedPaths.add(SerializedPathData(p, c, s))
        }
        return gson.toJson(serializedPaths)
    }

    /**
     * Converts a serialized string representation of paths back to a list of PathData objects.
     * @param pathDataString The serialized string representation of paths.
     * @return The list of PathData objects reconstructed from the serialized string.
     */
    @TypeConverter
    fun toPathDataList(pathDataString: String): ArrayList<SerializedPathData> {
        val type = object : TypeToken<ArrayList<SerializedPathData>>() {}.type
        return gson.fromJson(pathDataString, type)
    }

    /**
     * Converts a DbDrawing object to a Drawing object using file data and context.
     * @param dbDrawing The DbDrawing object to convert.
     * @param context The application context for file operations.
     * @return The Drawing object reconstructed from file data.
     */
    @TypeConverter
    fun toDrawing(dbDrawing: DbDrawing, context: Context): Drawing {
        val f = File(context.filesDir, dbDrawing.fileDir)
        val serializedPathData = toPathDataList(f.readText())
        val paths: ArrayList<PathData> = ArrayList()
        serializedPathData.forEach{ pathData ->
            val p = stringToPath(pathData.path)
            val c = pathData.color.toInt()
            val s = pathData.size.toFloat()
            paths.add(PathData(p, c, s))
        }
        val drawing = Drawing(paths, dbDrawing.name, dbDrawing.author)
        drawing.id = dbDrawing.id
        return drawing
    }

    fun toDrawing(data: String, name: String, author: String): Drawing{
        val serializedPathData = toPathDataList(data)
        val paths: ArrayList<PathData> = ArrayList()
        serializedPathData.forEach{ pathData ->
            val p = stringToPath(pathData.path)
            val c = pathData.color.toInt()
            val s = pathData.size.toFloat()
            paths.add(PathData(p, c, s))
        }
        val drawing = Drawing(paths, name, author)
        // TODO set the id
        return drawing
    }

}