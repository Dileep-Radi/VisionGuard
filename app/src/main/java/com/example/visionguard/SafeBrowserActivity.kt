package com.example.visionguard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class SafeBrowserActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var warningOverlay: TextView
    private lateinit var imageAnalysis: ImageAnalysis

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // Settings
    private var parentalMode = false
    private var safeDistance = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------- LOAD SETTINGS ----------
        val prefs = getSharedPreferences(
            SettingsActivity.PREFS,
            MODE_PRIVATE
        )
        parentalMode =
            prefs.getBoolean(SettingsActivity.KEY_PARENTAL, false)
        safeDistance =
            prefs.getInt(SettingsActivity.KEY_DISTANCE, 30)

        // ---------- UI ----------
        val root = FrameLayout(this)

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webViewClient = WebViewClient()
            loadUrl(intent.getStringExtra("URL") ?: "https://www.youtube.com")
        }

        warningOverlay = TextView(this).apply {
            text = "⚠️ Hold the phone farther away"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xAAFF0000.toInt())
            visibility = View.GONE
        }

        root.addView(webView)
        root.addView(warningOverlay)
        setContentView(root)

        // ---------- CAMERA PERMISSION ----------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }
    }

    // ================= CAMERA + ML =================
    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA

            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(
                    FaceDetectorOptions.PERFORMANCE_MODE_FAST
                )
                .build()

            val detector = FaceDetection.getClient(options)

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { proxy ->
                val mediaImage = proxy.image
                if (mediaImage != null) {

                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        proxy.imageInfo.rotationDegrees
                    )

                    detector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0]
                                val faceWidth =
                                    face.boundingBox.width()
                                val distance =
                                    estimateDistance(
                                        faceWidth,
                                        image.width
                                    )

                                if (distance < safeDistance) {
                                    runOnUiThread {
                                        handleUnsafe()
                                    }
                                } else {
                                    runOnUiThread {
                                        handleSafe()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    handleSafe()
                                }
                            }
                        }
                        .addOnCompleteListener {
                            proxy.close()
                        }
                } else proxy.close()
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(this))
    }

    // ================= DISTANCE =================
    private fun estimateDistance(
        faceWidthPx: Int,
        imageWidthPx: Int
    ): Double {

        val avgFaceWidthCm = 16.0
        val focalLengthMm = 4.2
        val sensorWidthMm = 6.3

        val ratio =
            faceWidthPx.toDouble() / imageWidthPx
        val faceOnSensor =
            sensorWidthMm * ratio

        return (focalLengthMm * avgFaceWidthCm) /
                faceOnSensor
    }

    // ================= BEHAVIOR =================
    private fun handleUnsafe() {
        warningOverlay.visibility = View.VISIBLE

        if (parentalMode) {
            // 🔒 STRICT MODE
            webView.evaluateJavascript(
                "document.querySelector('video')?.pause();",
                null
            )
            webView.setOnTouchListener { _, _ -> true }

            // Save report
            ReportsActivity.saveUnsafeEvent(
                this,
                webView.url ?: "Unknown"
            )
        }
    }

    private fun handleSafe() {
        warningOverlay.visibility = View.GONE

        if (parentalMode) {
            webView.setOnTouchListener(null)
            webView.onResume()
        }
    }

    // ================= PERMISSION =================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            results
        )
        if (requestCode == 101 &&
            results.isNotEmpty() &&
            results[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }
}
