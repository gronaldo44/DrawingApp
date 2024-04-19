#include <jni.h>
#include <vector>
#include <android/log.h>

// Define a Point class to represent coordinates
class Point {
public:
    float x;
    float y;

    // Constructor
    Point(float _x, float _y) : x(_x), y(_y) {}
};

// Represents path data containing the path, color, and size information.
class PathData {
public:
    std::vector<Point> path; // Vector of points representing the path
    int color;
    float size;

    // Constructor
    PathData(const std::vector<Point>& p, int c, float s) : path(p), color(c), size(s) {}
};

// Represents a regular drawing containing paths.
class Drawing {
private:
    std::vector<PathData> paths;
    int64_t id;

public:
    // Constructor
    Drawing(const std::vector<PathData>& p, int64_t i) : paths(p), id(i) {}

    // Function to increase the size of each path
    void increasePathSize(float scaleFactor) {
        for (auto& path : paths) {
            // Increase the size of the path
            path.size *= scaleFactor;
        }
    }
};


extern "C" {

JNIEXPORT void JNICALL
Java_com_example_drawingapp_viewmodel_DrawingViewModel_multPathSizeJIN(JNIEnv *env, jobject thiz, jobject drawingObj, jfloat scaleFactor) {
    // Get the class and method IDs for accessing fields and methods
    jclass drawingClass = env->GetObjectClass(drawingObj);
    jfieldID pathsFieldID = env->GetFieldID(drawingClass, "paths", "Ljava/util/ArrayList;");
    jclass arrayListClass = env->FindClass("java/util/ArrayList");
    jmethodID arrayListSizeMethodID = env->GetMethodID(arrayListClass, "size", "()I");
    jmethodID arrayListGetMethodID = env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");

    // Get the paths list from the Drawing object
    jobject pathsListObj = env->GetObjectField(drawingObj, pathsFieldID);
    jint numPaths = env->CallIntMethod(pathsListObj, arrayListSizeMethodID);

    // Iterate over each path and increase its size
    for (int i = 0; i < numPaths; ++i) {
        jobject pathDataObj = env->CallObjectMethod(pathsListObj, arrayListGetMethodID, i);

        // Get the class and method IDs for accessing fields in PathData
        jclass pathDataClass = env->GetObjectClass(pathDataObj);
        jfieldID sizeFieldID = env->GetFieldID(pathDataClass, "size", "F");

        // Get the current size of the path
        jfloat size = env->GetFloatField(pathDataObj, sizeFieldID);

        // Increase the size of the path
        size *= scaleFactor;

        // Set the updated size back to the PathData object
        env->SetFloatField(pathDataObj, sizeFieldID, size);

        // Release local references
        env->DeleteLocalRef(pathDataObj);
    }

    // Release local references
    env->DeleteLocalRef(pathsListObj);
}

JNIEXPORT void JNICALL
Java_com_example_drawingapp_viewmodel_DrawingViewModel_makePathsWhiteJIN(JNIEnv *env, jobject thiz,
                                                                         jobject drawing_obj) {
    jclass drawingClass = env->GetObjectClass(drawing_obj);
    jfieldID pathsFieldID = env->GetFieldID(drawingClass, "paths", "Ljava/util/ArrayList;");
    jobject pathsListObj = env->GetObjectField(drawing_obj, pathsFieldID);

    jclass arrayListClass = env->GetObjectClass(pathsListObj);
    jmethodID sizeMethodID = env->GetMethodID(arrayListClass, "size", "()I");
    jmethodID getMethodID = env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");

    jint pathsListSize = env->CallIntMethod(pathsListObj, sizeMethodID);
    for (int i = 0; i < pathsListSize; ++i) {
        jobject pathDataObj = env->CallObjectMethod(pathsListObj, getMethodID, i);
        jclass pathDataClass = env->GetObjectClass(pathDataObj);
        jfieldID colorFieldID = env->GetFieldID(pathDataClass, "color", "I");

        // Set the color to white (0xFFFFFFFF)
        env->SetIntField(pathDataObj, colorFieldID, 0xFFFFFFFF);
    }
}

JNIEXPORT void JNICALL
Java_com_example_drawingapp_viewmodel_DrawingViewModel_invertPathColorsJIN(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jobject drawing_obj) {
    jclass drawingClass = env->GetObjectClass(drawing_obj);
    jfieldID pathsFieldID = env->GetFieldID(drawingClass, "paths", "Ljava/util/ArrayList;");
    jobject pathsListObj = env->GetObjectField(drawing_obj, pathsFieldID);

    jclass arrayListClass = env->GetObjectClass(pathsListObj);
    jmethodID sizeMethodID = env->GetMethodID(arrayListClass, "size", "()I");
    jmethodID getMethodID = env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");

    jint pathsListSize = env->CallIntMethod(pathsListObj, sizeMethodID);
    for (int i = 0; i < pathsListSize; ++i) {
        jobject pathDataObj = env->CallObjectMethod(pathsListObj, getMethodID, i);
        jclass pathDataClass = env->GetObjectClass(pathDataObj);
        jfieldID colorFieldID = env->GetFieldID(pathDataClass, "color", "I");

        // Get the current color value
        jint color = env->GetIntField(pathDataObj, colorFieldID);

        // Invert the color by flipping each color component
        int alpha = (color >> 24) & 0xFF;
        int red = 255 - ((color >> 16) & 0xFF);
        int green = 255 - ((color >> 8) & 0xFF);
        int blue = 255 - (color & 0xFF);

        // Combine the inverted components into a new color integer
        jint invertedColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Set the new inverted color value
        env->SetIntField(pathDataObj, colorFieldID, invertedColor);
    }

}

}