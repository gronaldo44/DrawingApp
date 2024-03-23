package com.example.drawingapp.model

import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import java.util.Date.parse


/**
 * Regular Drawing class used in the rest of the project
 */
data class Drawing(val paths: ArrayList<PathData>){
    var id: Long = -1   // tmp value
}

data class SerializedPathData(val path: String, val color: String, val size: String)
data class PathData(val path: Path, val color: Int, val size: Float)

// DbDrawing class for database operations (serialization of paths)
@Entity(tableName = "Drawing")
data class DbDrawing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileDir: String // Directory to file containing this drawing
)

// Converter for Path to String and vice versa
object DrawingSerializer {
    private val gson = Gson()

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

    @TypeConverter
    fun toPathDataList(pathDataString: String): ArrayList<SerializedPathData> {
        val type = object : TypeToken<ArrayList<SerializedPathData>>() {}.type
        return gson.fromJson(pathDataString, type)
    }

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
        val drawing = Drawing(paths)
        drawing.id = dbDrawing.id
        return drawing
    }
}

