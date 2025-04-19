package com.example.lanedetectionopencv

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.graphics.ImageFormat
import android.media.Image
import java.nio.ByteBuffer

class MainActivity: FlutterActivity() {
    private val CHANNEL = "lane_detection_channel"

    external fun detectLanes(data: ByteArray, width: Int, height: Int): ByteArray

    init {
        System.loadLibrary("my_functions") // CMake'de tanımladığın .so dosyası adı
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            if (call.method == "detectLanes") {
                val data = call.argument<ByteArray>("data")!!
                val width = call.argument<Int>("width")!!
                val height = call.argument<Int>("height")!!
                val processed = detectLanes(data, width, height)
                result.success(processed)
            } else {
                result.notImplemented()
            }
        }
    }
}
