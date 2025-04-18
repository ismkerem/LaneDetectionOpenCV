#include <jni.h>
#include "C:\Users\ismke\Belgeler\lanedetectionopencv\include\opencv2\opencv.hpp"
#include <android/log.h>
#define LOG_TAG "LaneDetection"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

using namespace cv;

extern "C" {

JNIEXPORT jbyteArray JNICALL
Java_com_example_yourapp_MainActivity_detectLanes(JNIEnv *env, jobject thiz,
                                                  jbyteArray imageData, jint width, jint height) {
    // 1. Girdi verisini C++ tarafına al
    jbyte *dataPtr = env->GetByteArrayElements(imageData, nullptr);
    if (dataPtr == nullptr) {
        LOGD("Null image data");
        return nullptr;
    }

    // 2. YUV formatını OpenCV Mat’e çevir
    Mat yuvImg(height + height / 2, width, CV_8UC1, reinterpret_cast<unsigned char *>(dataPtr));
    Mat rgba;
    try {
        cvtColor(yuvImg, rgba, COLOR_YUV2RGBA_NV21);
    } catch (const cv::Exception& e) {
        LOGD("Color conversion error: %s", e.what());
        env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
        return nullptr;
    }

    // 3. Şerit tespiti işlemleri
    Mat gray, blurred, edges;
    cvtColor(rgba, gray, COLOR_RGBA2GRAY);
    GaussianBlur(gray, blurred, Size(5, 5), 0);
    Canny(blurred, edges, 50, 150);

    std::vector<Vec4i> lines;
    HoughLinesP(edges, lines, 1, CV_PI / 180, 50, 50, 10);

    // 4. Sonucu RGBA olarak oluştur
    Mat result;
    cvtColor(edges, result, COLOR_GRAY2RGBA);

    for (const auto &l : lines) {
        cv::line(result, Point(l[0], l[1]), Point(l[2], l[3]),
                 Scalar(0, 255, 0, 255), 2);
    }

    // 5. Sonucu byte array olarak Flutter’a gönder
    int size = result.total() * result.elemSize();
    jbyteArray resultArray = env->NewByteArray(size);
    if (resultArray == nullptr) {
        LOGD("Could not allocate result byte array");
        env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
        return nullptr;
    }

    env->SetByteArrayRegion(resultArray, 0, size, reinterpret_cast<jbyte *>(result.data));
    env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);

    return resultArray;
}

}
