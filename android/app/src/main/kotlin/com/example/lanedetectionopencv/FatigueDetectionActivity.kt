package com.example.lanedetectionopencv

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.*
import java.util.concurrent.Executors
import kotlin.math.hypot

class FatigueDetectionActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private val faceMeshDetector by lazy {
        val options = FaceMeshDetectorOptions.Builder()
        FaceMeshDetection.getClient()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fatigue_detection)

        previewView = findViewById(R.id.previewView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    faceMeshDetector.process(image)
                        .addOnSuccessListener { faces ->
                            processFaceMesh(faces)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            })

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFaceMesh(faces: List<FaceMesh>) {
        for (face in faces) {
            val landmarks = face.allPoints

            // Sol göz noktaları (örnek noktalar)
            val leftEyeTop = getPoint(landmarks, 159)
            val leftEyeBottom = getPoint(landmarks, 145)

            // Sağ göz noktaları (örnek noktalar)
            val rightEyeTop = getPoint(landmarks, 386)
            val rightEyeBottom = getPoint(landmarks, 374)

            if (leftEyeTop != null && leftEyeBottom != null && rightEyeTop != null && rightEyeBottom != null) {
                val leftEyeOpen = distance(leftEyeTop.first, leftEyeTop.second, leftEyeBottom.first, leftEyeBottom.second)
                val rightEyeOpen = distance(rightEyeTop.first, rightEyeTop.second, rightEyeBottom.first, rightEyeBottom.second)

                val avgEyeOpen = (leftEyeOpen + rightEyeOpen) / 2

                // Burada threshold koyuyoruz: gözler çok kapalıysa uyarı ver
                if (avgEyeOpen < 6.0) {  // Pixel cinsinden küçükse gözler kapalı demek
                    runOnUiThread {
                        Toast.makeText(this, "Yorgunluk Algılandı! Gözler Kapalı!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPoint(points: List<FaceMeshPoint>, index: Int): Pair<Float, Float>? {
        return points.getOrNull(index)?.let { Pair(it.position.x, it.position.y) }
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        return hypot((x2 - x1).toDouble(), (y2 - y1).toDouble())
    }
}
