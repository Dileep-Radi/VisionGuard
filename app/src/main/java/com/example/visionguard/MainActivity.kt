package com.example.visionguard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var webView: WebView
    private lateinit var alertText: TextView   // ⚠️ Alert overlay

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Root container
        val rootLayout = FrameLayout(this)

        // 🔹 Hidden Camera Preview
        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(1, 1) // 1x1 pixel, still gives frames
            visibility = View.INVISIBLE
        }
        rootLayout.addView(previewView)

        // 🔹 WebView (main UI)
        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://www.youtube.com") // Change this URL if needed
        }
        rootLayout.addView(webView)

        // 🔹 Alert TextView (added in Kotlin)
        alertText = TextView(this).apply {
            text = "⚠️ Your phone is Too Close! Keep at least 30 cm away."
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(30, 50, 30, 50)
            visibility = View.GONE
        }
        val alertParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        rootLayout.addView(alertText, alertParams)

        // 🔹 Set root layout as content
        setContentView(rootLayout)

        // ✅ Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
            val faceDetector = FaceDetection.getClient(options)

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    faceDetector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0]
                                val faceWidth = face.boundingBox.width()
                                val distance = estimateDistance(faceWidth, image.width)

                                if (distance < 25) {
                                    runOnUiThread {
                                        setScreenDim(true)
                                        alertText.visibility = View.VISIBLE
                                    }
                                } else {
                                    runOnUiThread {
                                        setScreenDim(false)
                                        alertText.visibility = View.GONE
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    setScreenDim(false)
                                    alertText.visibility = View.GONE
                                }
                            }
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun estimateDistance(faceWidthPx: Int, imageWidthPx: Int): Double {
        val averageFaceWidthCm = 16.0
        val focalLengthMm = 4.2
        val sensorWidthMm = 6.3

        val faceWidthRatio = faceWidthPx.toDouble() / imageWidthPx.toDouble()
        val faceWidthOnSensorMm = sensorWidthMm * faceWidthRatio

        return (focalLengthMm * averageFaceWidthCm) / faceWidthOnSensorMm
    }

    private fun setScreenDim(dim: Boolean) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = if (dim) 0.02f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = layoutParams
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        }
    }
}
