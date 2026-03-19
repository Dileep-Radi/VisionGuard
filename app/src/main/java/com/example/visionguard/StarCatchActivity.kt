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
    private lateinit var countdownView: TextView
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
        countdownView = findViewById(R.id.tvCountdown)
        btnStart = findViewById(R.id.btnStart)

        updateUI()
        btnStart.setOnClickListener { startGame() }
        star.setOnClickListener { onStarTouched() }
    }

    private fun updateUI() {
        scoreView.text = score.toString()
        roundView.text = "$round/$totalRounds"
    }

    private fun startGame() {
        if (isGameRunning) return
        isGameRunning = true
        btnStart.isEnabled = false
        round = 0
        score = 0
        updateUI()
        countdownView.text = "Game Starting..."
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
        instruction.text = "Round $round of $totalRounds"
        handler.postDelayed({ startCountdown() }, 400)
    }

    private fun startCountdown() {
        countdownView.text = "3"
        handler.postDelayed({ countdownView.text = "2" }, 1000)
        handler.postDelayed({ countdownView.text = "1" }, 2000)
        handler.postDelayed({ displayStar() }, 3000)
    }

    private fun displayStar() {
        instruction.text = "Tap the star!"
        countdownView.text = "3 seconds"

        // Ensure star is visible and properly configured
        star.visibility = android.view.View.VISIBLE
        star.text = "⭐"
        star.textSize = 80f
        star.alpha = 1f
        star.scaleX = 1f
        star.scaleY = 1f

        // Set random position
        val params = star.layoutParams as FrameLayout.LayoutParams
        params.gravity = positions[Random.nextInt(positions.size)]
        star.layoutParams = params

        // Set random color
        star.setTextColor(colors[Random.nextInt(colors.size)])

        // Schedule countdown updates and disappear
        handler.postDelayed({ updateCountdown(2) }, 1000)
        handler.postDelayed({ updateCountdown(1) }, 2000)
        handler.postDelayed({ disappearStar() }, 3000)
    }

    private fun updateCountdown(seconds: Int) {
        if (round < totalRounds) countdownView.text = "$seconds second${if (seconds > 1) "s" else ""}"
    }

    private fun disappearStar() {
        if (round < totalRounds) {
            if (!starTouched) {
                instruction.text = "Missed! Moving to next..."
                countdownView.text = "Next round in 2..."
            } else {
                instruction.text = "Great catch! Next in..."
                countdownView.text = "2 seconds"
            }
        }

        star.animate()
            .scaleX(0.3f)
            .scaleY(0.3f)
            .alpha(0.5f)
            .setDuration(200)
            .withEndAction {
                star.visibility = android.view.View.GONE
                handler.postDelayed({ startRound() }, 2000)
            }
            .start()
    }

    private fun onStarTouched() {
        if (!isGameRunning || starTouched || round >= totalRounds) return

        starTouched = true
        score++
        updateUI()
        instruction.text = "Perfect! +1 point"
        countdownView.text = "Great!"

        // Visual feedback - chained animation
        star.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(150)
            .withEndAction {
                star.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(150)
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
        countdownView.text = "Score: $score/6 ($accuracy%)"

        star.apply {
            visibility = android.view.View.VISIBLE
            text = "🎉"
            textSize = 80f

            val params = layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            layoutParams = params

            animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(500)
                .start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
