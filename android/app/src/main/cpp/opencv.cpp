#include <jni.h>
#include "include/opencv2/opencv.hpp"
#include <android/log.h>

#define LOG_TAG "LaneDetection"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

using namespace cv;

extern "C" {

JNIEXPORT jbyteArray JNICALL
Java_com_example_lanedetectionopencv_LaneDetectionActivity_00024Companion_detectLanes(JNIEnv *env, jobject thiz,
                                                              jbyteArray imageData, jint width, jint height) {
    jbyte *dataPtr = env->GetByteArrayElements(imageData, nullptr);
    if (dataPtr == nullptr) {
        LOGD("Null image data");
        return nullptr;
    }

    // YUV -> RGBA dönüşümü
    Mat yuvImg(height + height / 2, width, CV_8UC1, reinterpret_cast<unsigned char *>(dataPtr));
    Mat rgba;
    try {
        cvtColor(yuvImg, rgba, COLOR_YUV2RGBA_NV21);
    } catch (const cv::Exception& e) {
        LOGD("Color conversion error: %s", e.what());
        env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
        return nullptr;
    }

    // Telefonun dikey çekmesini düzelt (görüntüyü döndür)
    rotate(rgba, rgba, ROTATE_90_CLOCKWISE);

    // Gri tonlama ve blur (bulanıklaştırma)
    Mat gray, blurred, edges;
    cvtColor(rgba, gray, COLOR_RGBA2GRAY);
    GaussianBlur(gray, blurred, Size(5, 5), 0);

    // Kenar algılama (Canny)
    Canny(blurred, edges, 100, 200);

    // ROI - alt 1/2 bölge
    int newHeight = rgba.rows;
    int newWidth = rgba.cols;
    Rect roiRect(0, newHeight / 2, newWidth, newHeight / 2);  // Yalnızca alt yarıyı alıyoruz
    Mat roi = edges(roiRect);

    // Hough Transform
    std::vector<Vec4i> lines;
    HoughLinesP(roi, lines, 1, CV_PI / 180, 50, 100, 20);  // Parametreler önemli

    Mat result = rgba.clone();

    for (const auto &l : lines) {
        // Çizginin uzunluğunu kontrol et
        double length = sqrt(pow(l[2] - l[0], 2) + pow(l[3] - l[1], 2));
        if (length < 50) {
            continue; // Kısa çizgileri atla
        }

        // Çizginin açısını hesapla (yatay/dikey)
        double angle = atan2(l[3] - l[1], l[2] - l[0]) * 180.0 / CV_PI;
        angle = fabs(angle);

        // Açı aralığını filtrele
        if (angle > 30 && angle < 60) {  // Şerit çizgileri genellikle bu açı aralığında olur
            line(result,
                 Point(l[0], l[1] + newHeight / 2),
                 Point(l[2], l[3] + newHeight / 2),
                 Scalar(0, 255, 0, 255), 4);  // Çizgiyi yeşil renk ile çiz
        }
    }
    std::vector<Point> leftPoints;
    std::vector<Point> rightPoints;

    for (const auto &l : lines) {
        double length = sqrt(pow(l[2] - l[0], 2) + pow(l[3] - l[1], 2));
        if (length < 50) continue;

        double angle = atan2(l[3] - l[1], l[2] - l[0]) * 180.0 / CV_PI;
        angle = fabs(angle);

        if (angle > 30 && angle < 60) {
            Point pt1(l[0], l[1] + newHeight / 2);
            Point pt2(l[2], l[3] + newHeight / 2);

            // Sol mu sağ mı?
            if (pt1.x < newWidth / 2 && pt2.x < newWidth / 2) {
                leftPoints.push_back(pt1);
                leftPoints.push_back(pt2);
            } else if (pt1.x > newWidth / 2 && pt2.x > newWidth / 2) {
                rightPoints.push_back(pt1);
                rightPoints.push_back(pt2);
            }

            line(result, pt1, pt2, Scalar(0, 255, 0, 255), 4); // yeşil çizgi
        }
    }

// Eğer her iki tarafta da çizgiler varsa mavi alan çiz
    if (!leftPoints.empty() && !rightPoints.empty()) {
        // En alt ve en üst noktaları bul
        auto cmpY = [](const Point &a, const Point &b) { return a.y < b.y; };
        Point leftTop = *std::min_element(leftPoints.begin(), leftPoints.end(), cmpY);
        Point leftBottom = *std::max_element(leftPoints.begin(), leftPoints.end(), cmpY);
        Point rightTop = *std::min_element(rightPoints.begin(), rightPoints.end(), cmpY);
        Point rightBottom = *std::max_element(rightPoints.begin(), rightPoints.end(), cmpY);

        // Alanı doldur
        std::vector<Point> fillPolyPoints = {
                leftTop, rightTop, rightBottom, leftBottom
        };

        fillConvexPoly(result, fillPolyPoints, Scalar(255, 0, 0, 100)); // mavi (alpha: 100)
    }


    // Sonuç görüntüsünü PNG formatında encode et
    std::vector<uchar> buffer;
    try {
        imencode(".png", result, buffer);
    } catch (const cv::Exception& e) {
        LOGD("PNG encoding error: %s", e.what());
        env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
        return nullptr;
    }

    // Sonuç byte dizisini geri gönder
    jbyteArray resultArray = env->NewByteArray(buffer.size());
    if (resultArray == nullptr) {
        LOGD("Could not allocate result byte array");
        env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
        return nullptr;
    }

    env->SetByteArrayRegion(resultArray, 0, buffer.size(), reinterpret_cast<jbyte *>(buffer.data()));
    env->ReleaseByteArrayElements(imageData, dataPtr, JNI_ABORT);
    return resultArray;
}

}
