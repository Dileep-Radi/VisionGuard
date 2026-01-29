package com.example.visionguard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.ProgressBar
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
class BlinkChallengeActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvBlinkCount: TextView
    private lateinit var tvInstruction: TextView
    private lateinit var tvDebug: TextView
    private lateinit var progressBar: ProgressBar

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var imageAnalysis: ImageAnalysis

    private var blinkCount = 0
    private val targetBlink = 10

    // Blink state
    private var eyesClosed = false
    private var lastBlinkTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blink_game)

        // ---------- UI ----------
        tvTitle = findViewById(R.id.tvGameTitle)
        tvBlinkCount = findViewById(R.id.tvBlinkCount)
        tvInstruction = findViewById(R.id.tvInstruction)
        tvDebug = findViewById(R.id.tvDebug)
        progressBar = findViewById(R.id.progressBlink)

        tvBlinkCount.text = "0"
        progressBar.max = targetBlink
        progressBar.progress = 0

        // ---------- Camera Permission ----------
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

    // ================= CAMERA + ML KIT =================
    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)

        providerFuture.addListener({
            val provider = providerFuture.get()

            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build()

            val detector = FaceDetection.getClient(options)

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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
                                val left = face.leftEyeOpenProbability ?: -1f
                                val right = face.rightEyeOpenProbability ?: -1f
                                handleBlink(left, right)
                            }
                        }
                        .addOnCompleteListener {
                            proxy.close()
                        }
                } else {
                    proxy.close()
                }
            }

            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(this))
    }

    // ================= BLINK LOGIC =================
    private fun handleBlink(left: Float, right: Float) {

        if (left < 0 || right < 0) {
            runOnUiThread {
                tvDebug.text = "Face not clear"
            }
            return
        }

        val eyesAreClosed = left < 0.5f && right < 0.5f
        val eyesAreOpen = left > 0.6f && right > 0.6f
        val now = System.currentTimeMillis()

        runOnUiThread {
            tvDebug.text =
                "L:${"%.2f".format(left)}  R:${"%.2f".format(right)}"
        }

        // OPEN → CLOSED
        if (!eyesClosed && eyesAreClosed) {
            eyesClosed = true
            lastBlinkTime = now
        }

        // CLOSED → OPEN = BLINK
        if (eyesClosed && eyesAreOpen) {
            val duration = now - lastBlinkTime

            if (duration in 100..800) {
                blinkCount++
                runOnUiThread {
                    tvBlinkCount.text = blinkCount.toString()
                    progressBar.progress = blinkCount
                }
            }
            eyesClosed = false

            if (blinkCount >= targetBlink) {
                finishGame()
            }
        }
    }

    // ================= FINISH =================
    private fun finishGame() {
        tvTitle.text = "🎉 Blink Challenge Complete!"
        tvInstruction.text = "Great job! Eyes refreshed 👀"
        tvDebug.text = "Completed"
        imageAnalysis.clearAnalyzer()
    }

    // ================= PERMISSION =================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }
}
