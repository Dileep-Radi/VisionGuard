package com.example.visionguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StarCatchActivity : AppCompatActivity() {

    private lateinit var mazeView: MazeView
    private lateinit var tvTime: TextView
    private lateinit var tvMoves: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button
    private lateinit var btnUp: Button
    private lateinit var btnLeft: Button
    private lateinit var btnDown: Button
    private lateinit var btnRight: Button

    private var isGameRunning = false
    private var moves = 0
    private var elapsedSeconds = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_catch)

        mazeView = findViewById(R.id.mazeView)
        tvTime = findViewById(R.id.tvTime)
        tvMoves = findViewById(R.id.tvMoves)
        tvStatus = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStart)
        btnUp = findViewById(R.id.btnUp)
        btnLeft = findViewById(R.id.btnLeft)
        btnDown = findViewById(R.id.btnDown)
        btnRight = findViewById(R.id.btnRight)

        btnStart.setOnClickListener { startGame() }
        btnUp.setOnClickListener { movePlayer(0, -1) }
        btnLeft.setOnClickListener { movePlayer(-1, 0) }
        btnDown.setOnClickListener { movePlayer(0, 1) }
        btnRight.setOnClickListener { movePlayer(1, 0) }

        updateUI()
    }

    private fun startGame() {
        if (isGameRunning) return

        isGameRunning = true
        moves = 0
        elapsedSeconds = 0
        btnStart.isEnabled = false
        btnStart.text = "Game Running..."

        mazeView.reset()
        tvStatus.text = "Find the star!"
        tvMoves.text = "0"
        tvTime.text = "0s"

        startTimer()
    }

    private fun movePlayer(dx: Int, dy: Int) {
        if (!isGameRunning) return

        if (mazeView.movePlayer(dx, dy)) {
            moves++
            tvMoves.text = moves.toString()

            // Check if star is found
            if (mazeView.isStarFound()) {
                finishGame()
            }
        }
    }

    private fun startTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    elapsedSeconds++
                    tvTime.text = "${elapsedSeconds}s"
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun finishGame() {
        isGameRunning = false
        btnStart.isEnabled = true
        btnStart.text = "Play Again"

        val rating = when {
            moves <= 20 -> "Perfect! 🌟"
            moves <= 30 -> "Great! ✓"
            moves <= 40 -> "Good"
            else -> "Keep Trying"
        }

        tvStatus.text = rating

        // Show celebration
        mazeView.invalidate()
    }

    private fun updateUI() {
        tvTime.text = "0s"
        tvMoves.text = "0"
        tvStatus.text = "Ready"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
