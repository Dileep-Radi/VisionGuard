package com.example.visionguard

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class StarCatchActivity : AppCompatActivity() {

    private lateinit var star: TextView
    private lateinit var instruction: TextView
    private lateinit var scoreView: TextView

    private val handler = Handler(Looper.getMainLooper())

    private var round = 0
    private val totalRounds = 6
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_catch)

        star = findViewById(R.id.tvStar)
        instruction = findViewById(R.id.tvInstruction)
        scoreView = findViewById(R.id.tvScore)

        instruction.text = "👀 Follow the star with your eyes"
        scoreView.text = "Score: 0"

        startRound()
    }

    // ---------------- GAME FLOW ----------------
    private fun startRound() {
        if (round >= totalRounds) {
            finishGame()
            return
        }

        round++
        countdown(3)
    }

    // ---------------- COUNTDOWN ----------------
    private fun countdown(time: Int) {
        if (time == 0) {
            moveStar()
            return
        }

        instruction.text = "Next move in $time..."
        handler.postDelayed({
            countdown(time - 1)
        }, 700)
    }

    // ---------------- MOVE STAR ----------------
    private fun moveStar() {
        instruction.text = "Look at the ⭐ for 3 seconds"
        score++

        scoreView.text = "Score: $score"

        // Random position
        val params = star.layoutParams as FrameLayout.LayoutParams
        params.gravity = randomGravity()
        star.layoutParams = params

        // Random color
        star.setTextColor(randomColor())

        // Small animation effect
        star.scaleX = 0.5f
        star.scaleY = 0.5f
        star.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(300)
            .start()

        handler.postDelayed({
            startRound()
        }, 3000)
    }

    // ---------------- FINISH ----------------
    private fun finishGame() {
        instruction.text = "🎉 Eye Exercise Complete!"
        scoreView.text = "Final Score: $score ⭐"

        star.text = "👀"
        star.textSize = 60f
        star.setTextColor(Color.GREEN)

        val params = star.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        star.layoutParams = params
    }

    // ---------------- RANDOM HELPERS ----------------
    private fun randomGravity(): Int {
        val positions = listOf(
            Gravity.TOP or Gravity.START,
            Gravity.TOP or Gravity.END,
            Gravity.BOTTOM or Gravity.START,
            Gravity.BOTTOM or Gravity.END,
            Gravity.CENTER
        )
        return positions[Random.nextInt(positions.size)]
    }

    private fun randomColor(): Int {
        val colors = listOf(
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.GREEN,
            Color.RED
        )
        return colors[Random.nextInt(colors.size)]
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
