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
        handler.postDelayed({
            startRound()
        }, 500)
    }

    private fun startRound() {
        if (round >= totalRounds) {
            finishGame()
            return
        }

        round++
        starTouched = false
        updateUI()
        
        // Show countdown before star appears
        countdownView.text = "Get Ready..."
        instruction.text = "Round $round of $totalRounds"
        
        handler.postDelayed({
            showCountdown(3)
        }, 400)
    }

    private fun showCountdown(seconds: Int) {
        if (seconds == 0) {
            displayStar()
            return
        }

        countdownView.text = seconds.toString()
        handler.postDelayed({
            showCountdown(seconds - 1)
        }, 1000)
    }

    private fun displayStar() {
        instruction.text = "Tap the star!"
        countdownView.text = "3 seconds"

        // Make star visible
        star.visibility = android.view.View.VISIBLE

        // Set random position
        val params = star.layoutParams as FrameLayout.LayoutParams
        params.gravity = randomGravity()
        star.layoutParams = params

        // Set random color
        star.setTextColor(randomColor())

        // Appear animation
        star.scaleX = 0.3f
        star.scaleY = 0.3f
        star.alpha = 0.5f
        star.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(300)
            .start()

        // Countdown during star display
        handler.postDelayed({
            if (round < totalRounds) {
                countdownView.text = "2 seconds"
            }
        }, 1000)

        handler.postDelayed({
            if (round < totalRounds) {
                countdownView.text = "1 second"
            }
        }, 2000)

        // Star disappears after 3 seconds
        handler.postDelayed({
            if (round < totalRounds && !starTouched) {
                // Missed the star
                instruction.text = "Missed! Moving to next..."
                countdownView.text = "Next round in 2..."
            } else if (round < totalRounds) {
                // Already tapped, waiting for next
                instruction.text = "Great catch! Next in..."
                countdownView.text = "2 seconds"
            }
            
            // Disappear animation
            star.animate()
                .scaleX(0.3f)
                .scaleY(0.3f)
                .alpha(0.5f)
                .setDuration(200)
                .withEndAction {
                    star.visibility = android.view.View.GONE
                }
                .start()

            // Move to next round after delay
            handler.postDelayed({
                startRound()
            }, 2000)
        }, 3000)
    }

    private fun onStarTouched() {
        if (!isGameRunning || starTouched || round >= totalRounds) return
        
        starTouched = true
        score++
        updateUI()
        
        // Visual feedback
        star.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(150)
            .start()
        
        star.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(150)
            .setStartDelay(150)
            .start()

        instruction.text = "Perfect! +1 point"
        countdownView.text = "Great!"
    }

    private fun finishGame() {
        isGameRunning = false
        btnStart.text = "Play Again"
        btnStart.isEnabled = true

        instruction.text = "Game Complete!"
        
        // Calculate accuracy
        val accuracy = (score * 100) / totalRounds
        countdownView.text = "Score: $score/6 ($accuracy%)"

        // Celebration animation
        star.apply {
            visibility = android.view.View.VISIBLE
            text = "🎉"
            textSize = 80f
            animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(500)
                .start()
            
            val params = layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            layoutParams = params
        }
    }

    private fun randomGravity(): Int {
        val positions = listOf(
            Gravity.TOP or Gravity.START,
            Gravity.TOP or Gravity.END,
            Gravity.CENTER_VERTICAL or Gravity.START,
            Gravity.CENTER_VERTICAL or Gravity.END,
            Gravity.BOTTOM or Gravity.START,
            Gravity.BOTTOM or Gravity.END,
            Gravity.CENTER
        )
        return positions[Random.nextInt(positions.size)]
    }

    private fun randomColor(): Int {
        val colors = listOf(
            0xFFD700,   // Gold
            0x38BDF8,   // Cyan
            0xFF1493,   // Deep Pink
            0x22C55E,   // Green
            0xFFA500    // Orange
        )
        return colors[Random.nextInt(colors.size)].toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
