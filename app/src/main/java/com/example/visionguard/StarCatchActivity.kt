package com.example.visionguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class StarCatchActivity : AppCompatActivity() {

    private lateinit var star: TextView
    private lateinit var instruction: TextView
    private lateinit var scoreView: TextView
    private lateinit var roundView: TextView
    private lateinit var accuracyView: TextView
    private lateinit var countdownView: TextView
    private lateinit var statusView: TextView
    private lateinit var btnStart: Button
    private val handler = Handler(Looper.getMainLooper())

    private var round = 0
    private val totalRounds = 6
    private var score = 0
    private var isGameRunning = false
    private var starTouched = false

    // Cached for performance
    private val positions = intArrayOf(
        Gravity.TOP or Gravity.START,
        Gravity.TOP or Gravity.END,
        Gravity.CENTER_VERTICAL or Gravity.START,
        Gravity.CENTER_VERTICAL or Gravity.END,
        Gravity.BOTTOM or Gravity.START,
        Gravity.BOTTOM or Gravity.END,
        Gravity.CENTER
    )

    private val colors = intArrayOf(
        0x000000.toInt()    // Black
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_catch)

        star = findViewById(R.id.tvStar)
        instruction = findViewById(R.id.tvInstruction)
        scoreView = findViewById(R.id.tvScore)
        roundView = findViewById(R.id.tvRound)
        accuracyView = findViewById(R.id.tvAccuracy)
        countdownView = findViewById(R.id.tvCountdown)
        statusView = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStart)

        // Initialize star properties
        star.text = "⭐"
        star.textSize = 80f
        star.setTextColor(0xFF000000.toInt())
        star.alpha = 1f
        star.scaleX = 1f
        star.scaleY = 1f

        updateUI()
        btnStart.setOnClickListener { startGame() }
        star.setOnClickListener { onStarTouched() }
    }

    private fun updateUI() {
        scoreView.text = score.toString()
        roundView.text = "$round/$totalRounds"
        val accuracy = if (round > 0) (score * 100) / round else 0
        accuracyView.text = "$accuracy%"
    }

    private fun startGame() {
        if (isGameRunning) return
        isGameRunning = true
        btnStart.isEnabled = false
        btnStart.text = "Game Running..."
        round = 0
        score = 0
        updateUI()
        countdownView.text = "Starting..."
        statusView.text = "Get ready!"
        handler.postDelayed({ startRound() }, 500)
    }

    private fun startRound() {
        if (round >= totalRounds) {
            finishGame()
            return
        }

        round++
        starTouched = false
        updateUI()
        countdownView.text = "Get Ready..."
        statusView.text = "Round $round of $totalRounds"
        handler.postDelayed({ startCountdown() }, 400)
    }

    private fun startCountdown() {
        countdownView.text = "3"
        statusView.text = ""
        handler.postDelayed({ countdownView.text = "2" }, 1000)
        handler.postDelayed({ countdownView.text = "1" }, 2000)
        handler.postDelayed({ displayStar() }, 3000)
    }

    private fun displayStar() {
        instruction.text = "Tap the star!"
        countdownView.text = "3"

        // Set random position first
        val params = star.layoutParams as FrameLayout.LayoutParams
        params.gravity = positions[Random.nextInt(positions.size)]
        star.layoutParams = params

        // Configure star appearance BEFORE making visible
        star.text = "⭐"
        star.textSize = 80f
        star.alpha = 1f
        star.scaleX = 1f
        star.scaleY = 1f
        star.setTextColor(0xFF000000.toInt())

        statusView.text = "TAP the star!"

        // Ensure visibility and layout are processed
        star.visibility = android.view.View.VISIBLE
        star.invalidate()
        star.requestLayout()

        // Schedule countdown updates and disappear
        handler.postDelayed({ updateCountdown(2) }, 1000)
        handler.postDelayed({ updateCountdown(1) }, 2000)
        handler.postDelayed({ disappearStar() }, 3000)
    }

    private fun updateCountdown(seconds: Int) {
        if (round < totalRounds) countdownView.text = seconds.toString()
    }

    private fun disappearStar() {
        if (round < totalRounds) {
            if (!starTouched) {
                instruction.text = "Missed!"
                countdownView.text = "Missed"
                statusView.text = "Move to next round..."
            } else {
                instruction.text = "Great catch!"
                countdownView.text = "Got it!"
                statusView.text = "Nice! Moving to next..."
            }
        }

        star.animate()
            .scaleX(0.3f)
            .scaleY(0.3f)
            .alpha(0.5f)
            .setDuration(200)
            .withEndAction {
                star.visibility = android.view.View.GONE
                handler.postDelayed({ startRound() }, 1500)
            }
            .start()
    }

    private fun onStarTouched() {
        if (!isGameRunning || starTouched || round >= totalRounds) return

        starTouched = true
        score++
        updateUI()
        instruction.text = "Perfect hit! ✓"
        statusView.text = "+1 Point"

        // Visual feedback - scale up and down
        star.animate()
            .scaleX(1.4f)
            .scaleY(1.4f)
            .setDuration(120)
            .withEndAction {
                star.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun finishGame() {
        isGameRunning = false
        btnStart.text = "Play Again"
        btnStart.isEnabled = true

        instruction.text = "Game Complete!"
        val accuracy = (score * 100) / totalRounds
        countdownView.text = "Score: $score/6"
        statusView.text = "Accuracy: $accuracy%"

        star.apply {
            visibility = android.view.View.VISIBLE
            text = "🎉"
            textSize = 80f

            val params = layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            layoutParams = params

            animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(600)
                .start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
